package br.com.nexoimob.services;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.*;
import br.com.nexoimob.repositories.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AgendaService {
    private final AtividadeRepository atividades;
    private final VisitaRepository visitas;
    private final LeadService leads;
    private final ImovelService imoveis;
    private final AcessoService acesso;
    private final JdbcTemplate jdbc;

    public AgendaService(AtividadeRepository atividades, VisitaRepository visitas,
            LeadService leads, ImovelService imoveis, AcessoService acesso,
            JdbcTemplate jdbc) {
        this.atividades = atividades;
        this.visitas = visitas;
        this.leads = leads;
        this.imoveis = imoveis;
        this.acesso = acesso;
        this.jdbc = jdbc;
    }

    public List<Atividade> listarAtividades() {
        if (acesso.ehAdministrador()) {
            return atividades.findAllByOrderByPrazoAsc();
        }
        return atividades.findByLeadResponsavelIdOrderByPrazoAsc(acesso.usuarioAtual().getId());
    }

    public List<Visita> listarVisitas() {
        if (acesso.ehAdministrador()) {
            return visitas.findAllByOrderByAgendadaEmDesc();
        }
        return visitas.findByLeadResponsavelIdOrderByAgendadaEmDesc(acesso.usuarioAtual().getId());
    }

    public List<Atividade> atividadesDoLead(Long id) {
        leads.buscar(id);
        return atividades.findByLeadId(id);
    }

    public List<Visita> visitasDoLead(Long id) {
        leads.buscar(id);
        return visitas.findByLeadId(id);
    }

    public Atividade buscarAtividade(Long id) {
        Atividade atividade = atividades.findById(id).orElseThrow();
        acesso.verificarLead(atividade.getLead());
        leads.exigirAberto(atividade.getLead());
        return atividade;
    }

    public Visita buscarVisita(Long id) {
        Visita visita = visitas.findById(id).orElseThrow();
        acesso.verificarLead(visita.getLead());
        leads.exigirAberto(visita.getLead());
        return visita;
    }

    public void salvarAtividade(Long id, AtividadeForm form) {
        // Confere o vínculo atual antes de aceitar a alteração.
        Atividade atividade;
        if (id == null) {
            atividade = new Atividade();
        } else {
            atividade = buscarAtividade(id);
        }
        Lead lead = leads.buscar(form.getLeadId());
        leads.exigirAberto(lead);
        atividade.setLead(lead);
        atividade.setTitulo(form.getTitulo());
        atividade.setTipo(form.getTipo());
        atividade.setPrazo(form.getPrazo());
        atividade.setObservacoes(form.getObservacoes());
        if (form.isConcluida()) {
            if (atividade.getConcluidaEm() == null) {
                atividade.setConcluidaEm(LocalDateTime.now());
            }
        } else {
            atividade.setConcluidaEm(null);
        }
        atividades.save(atividade);
        boolean contato = form.getTipo() == TipoAtividade.LIGACAO
                || form.getTipo() == TipoAtividade.MENSAGEM;
        if (form.isConcluida() && contato && lead.getEtapa() == Etapa.NOVO) {
            leads.mover(lead.getId(), Etapa.CONTATADO, null);
        }
    }

    public void salvarVisita(Long id, VisitaForm form) {
        Visita visita;
        if (id == null) {
            visita = new Visita();
        } else {
            visita = buscarVisita(id);
        }
        Lead lead = leads.buscar(form.getLeadId());
        leads.exigirAberto(lead);
        Imovel imovel = imoveis.buscar(form.getImovelId());
        leads.exigirInteresse(lead.getId(), imovel.getId());
        if (!imoveis.disponivel(imovel)) {
            throw new IllegalArgumentException("Imóvel indisponível");
        }
        if (form.isVirtual() && form.getKm().signum() != 0) {
            throw new IllegalArgumentException("Visita virtual deve ter zero quilômetros");
        }
        if (id == null) {
            jdbc.queryForObject("EXEC dbo.sp_agendar_visita ?,?,?,?,?,?,?,?", Long.class,
                    lead.getId(), imovel.getId(), form.getAgendadaEm(),
                    form.getSituacao().name(), form.isVirtual(), form.getKm(),
                    form.getObservacoes(), acesso.usuarioAtual().getId());
            return;
        }
        visita.setLead(lead);
        visita.setImovel(imovel);
        visita.setAgendadaEm(form.getAgendadaEm());
        visita.setSituacao(form.getSituacao());
        visita.setVirtual(form.isVirtual());
        visita.setKm(form.getKm());
        visita.setObservacoes(form.getObservacoes());
        visitas.saveAndFlush(visita);
    }

    public void excluirAtividade(Long id) {
        atividades.delete(buscarAtividade(id));
    }

    public void excluirVisita(Long id) {
        visitas.delete(buscarVisita(id));
    }
}
