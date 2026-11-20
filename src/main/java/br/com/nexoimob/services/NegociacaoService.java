package br.com.nexoimob.services;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.PropostaForm;
import br.com.nexoimob.repositories.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NegociacaoService {
    private final PropostaRepository propostas;
    private final LeadService leads;
    private final ImovelService imoveis;
    private final AcessoService acesso;
    private final JdbcTemplate jdbc;

    public NegociacaoService(PropostaRepository propostas, LeadService leads, ImovelService imoveis,
            AcessoService acesso, JdbcTemplate jdbc) {
        this.propostas = propostas;
        this.leads = leads;
        this.imoveis = imoveis;
        this.acesso = acesso;
        this.jdbc = jdbc;
    }

    public List<Proposta> listar() {
        if (acesso.ehAdministrador()) {
            return propostas.findAllByOrderByCriadaEmDesc();
        }
        return propostas.findByLeadResponsavelIdOrderByCriadaEmDesc(acesso.usuarioAtual().getId());
    }

    public List<Proposta> propostasDoLead(Long id) {
        leads.buscar(id);
        return propostas.findByLeadId(id);
    }

    public Proposta buscarParaEditar(Long id) {
        Proposta proposta = propostas.findById(id).orElseThrow();
        acesso.verificarLead(proposta.getLead());
        leads.exigirAberto(proposta.getLead());
        if (proposta.getSituacao() == SituacaoProposta.ACEITA) {
            throw new IllegalArgumentException("Proposta aceita não pode ser editada");
        }
        return proposta;
    }

    public Long salvar(Long id, PropostaForm form) {
        Proposta proposta;
        if (id == null) {
            proposta = new Proposta();
        } else {
            proposta = buscarParaEditar(id);
        }
        Lead lead = leads.buscar(form.getLeadId());
        leads.exigirAberto(lead);
        Imovel imovel = imoveis.buscar(form.getImovelId());
        leads.exigirInteresse(lead.getId(), imovel.getId());
        if (!imoveis.disponivel(imovel)) {
            throw new IllegalArgumentException("Imóvel indisponível");
        }
        if (form.getSituacao() == SituacaoProposta.ACEITA) {
            throw new IllegalArgumentException("Use a ação Aceitar proposta na ficha");
        }
        proposta.setLead(lead);
        proposta.setImovel(imovel);
        proposta.setValor(form.getValor());
        proposta.setSituacao(form.getSituacao());
        proposta.setObservacoes(form.getObservacoes());
        if (id == null) {
            proposta.setCriadaEm(LocalDateTime.now());
        }
        propostas.saveAndFlush(proposta);
        return lead.getId();
    }

    public Long aceitar(Long id) {
        Proposta proposta = propostas.findById(id).orElseThrow();
        Long leadId = proposta.getLead().getId();
        acesso.verificarLead(proposta.getLead());
        jdbc.update("EXEC dbo.sp_aceitar_proposta ?,?", id, acesso.usuarioAtual().getId());
        return leadId;
    }

    public void excluir(Long id) {
        propostas.delete(buscarParaEditar(id));
    }
}
