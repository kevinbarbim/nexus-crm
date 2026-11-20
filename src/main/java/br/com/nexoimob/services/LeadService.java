package br.com.nexoimob.services;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.LeadForm;
import br.com.nexoimob.repositories.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LeadService {
    private final LeadRepository leads;
    private final UsuarioRepository usuarios;
    private final InteresseRepository interesses;
    private final HistoricoEtapaRepository historicos;
    private final AtividadeRepository atividades;
    private final VisitaRepository visitas;
    private final PropostaRepository propostas;
    private final OcorrenciaRepository ocorrencias;
    private final ImovelRepository imoveis;
    private final AcessoService acesso;
    private final JdbcTemplate jdbc;

    public LeadService(LeadRepository leads, UsuarioRepository usuarios,
            InteresseRepository interesses, HistoricoEtapaRepository historicos,
            AtividadeRepository atividades, VisitaRepository visitas,
            PropostaRepository propostas, OcorrenciaRepository ocorrencias,
            ImovelRepository imoveis, AcessoService acesso, JdbcTemplate jdbc) {
        this.leads = leads;
        this.usuarios = usuarios;
        this.interesses = interesses;
        this.historicos = historicos;
        this.atividades = atividades;
        this.visitas = visitas;
        this.propostas = propostas;
        this.ocorrencias = ocorrencias;
        this.imoveis = imoveis;
        this.acesso = acesso;
        this.jdbc = jdbc;
    }

    public List<Lead> listar() {
        if (acesso.ehAdministrador()) {
            return leads.findAllByOrderByCriadoEmDesc();
        }
        return leads.findByResponsavelIdOrderByCriadoEmDesc(acesso.usuarioAtual().getId());
    }

    public List<Lead> listarAbertos() {
        List<Lead> abertos = new ArrayList<>();
        for (Lead lead : listar()) {
            if (!encerrado(lead)) {
                abertos.add(lead);
            }
        }
        return abertos;
    }

    public Lead buscar(Long id) {
        Lead lead = leads.findById(id).orElseThrow();
        acesso.verificarLead(lead);
        return lead;
    }

    public boolean encerrado(Lead lead) {
        return lead.getEtapa() == Etapa.GANHO || lead.getEtapa() == Etapa.PERDIDO;
    }

    public void exigirAberto(Lead lead) {
        if (encerrado(lead)) {
            throw new IllegalArgumentException("O atendimento já foi encerrado");
        }
    }

    public Lead salvar(Long id, LeadForm form) {
        boolean novo = id == null;
        Lead lead;
        if (novo) {
            lead = new Lead();
        } else {
            lead = buscar(id);
        }
        Usuario responsavel = acesso.usuarioAtual();
        if (acesso.ehAdministrador()) {
            responsavel = usuarios.findById(form.getResponsavelId()).orElseThrow();
        }
        if (!responsavel.isAtivo()) {
            throw new IllegalArgumentException("Escolha um responsável ativo");
        }
        lead.setNome(form.getNome().trim());
        lead.setEmail(form.getEmail());
        lead.setTelefone(form.getTelefone().trim());
        lead.setOrigem(form.getOrigem());
        lead.setOrcamento(form.getOrcamento());
        lead.setObservacoes(form.getObservacoes());
        lead.setResponsavel(responsavel);
        lead.setAtualizadoPor(acesso.usuarioAtual());
        if (novo) {
            lead.setCriadoEm(LocalDateTime.now());
            lead.setEtapa(Etapa.NOVO);
        }
        leads.saveAndFlush(lead);
        return lead;
    }

    public void mover(Long id, Etapa etapa, String motivo) {
        Lead lead = buscar(id);
        exigirAberto(lead);
        if (etapa == Etapa.GANHO) {
            throw new IllegalArgumentException("Para registrar ganho, aceite uma proposta");
        }
        if (etapa == Etapa.PERDIDO) {
            if (motivo == null || motivo.isBlank()) {
                throw new IllegalArgumentException("Informe o motivo da perda");
            }
            try {
                MotivoPerda.valueOf(motivo);
            } catch (IllegalArgumentException erro) {
                throw new IllegalArgumentException("Selecione um motivo de perda válido");
            }
        }
        jdbc.update("EXEC dbo.sp_mover_lead ?,?,?,?", id, etapa.name(), motivo,
                acesso.usuarioAtual().getId());
    }

    public List<Interesse> interessesDoLead(Long id) {
        buscar(id); // Valida o acesso.
        return interesses.findByLeadId(id);
    }

    public List<HistoricoEtapa> historicoDoLead(Long id) {
        buscar(id);
        return historicos.findByLeadIdOrderByAlteradaEmDesc(id);
    }

    public void adicionarInteresse(Long leadId, Long imovelId) {
        Lead lead = buscar(leadId);
        exigirAberto(lead);
        Imovel imovel = imoveis.findById(imovelId).orElseThrow();
        if (imovel.getSituacao() != SituacaoImovel.DISPONIVEL
                && imovel.getSituacao() != SituacaoImovel.RESERVADO) {
            throw new IllegalArgumentException("Imóvel indisponível");
        }
        if (interesses.existsByLeadIdAndImovelId(leadId, imovelId)) {
            throw new IllegalArgumentException("Este interesse já está registrado");
        }
        Interesse interesse = new Interesse();
        interesse.setLead(lead);
        interesse.setImovel(imovel);
        interesse.setCriadoEm(LocalDateTime.now());
        interesses.save(interesse);
    }

    public void exigirInteresse(Long leadId, Long imovelId) {
        if (!interesses.existsByLeadIdAndImovelId(leadId, imovelId)) {
            throw new IllegalArgumentException("Vincule o imóvel aos interesses do lead primeiro");
        }
    }

    public Long removerInteresse(Long id) {
        Interesse interesse = interesses.findById(id).orElseThrow();
        acesso.verificarLead(interesse.getLead());
        exigirAberto(interesse.getLead());
        Long leadId = interesse.getLead().getId();
        Long imovelId = interesse.getImovel().getId();
        for (Visita visita : visitas.findByLeadId(leadId)) {
            if (visita.getImovel().getId().equals(imovelId)) {
                throw new IllegalArgumentException("O interesse já foi utilizado em visita");
            }
        }
        for (Proposta proposta : propostas.findByLeadId(leadId)) {
            if (proposta.getImovel().getId().equals(imovelId)) {
                throw new IllegalArgumentException("O interesse já foi utilizado em proposta");
            }
        }
        interesses.delete(interesse);
        return leadId;
    }

    public void excluir(Long id) {
        Lead lead = buscar(id);
        exigirAberto(lead);
        // Exclui os vínculos antes do atendimento.
        ocorrencias.deleteAll(ocorrencias.findByLeadId(id));
        atividades.deleteAll(atividades.findByLeadId(id));
        visitas.deleteAll(visitas.findByLeadId(id));
        propostas.deleteAll(propostas.findByLeadId(id));
        interesses.deleteAll(interesses.findByLeadId(id));
        historicos.deleteAll(historicos.findByLeadIdOrderByAlteradaEmDesc(id));
        leads.delete(lead);
        leads.flush();
    }
}
