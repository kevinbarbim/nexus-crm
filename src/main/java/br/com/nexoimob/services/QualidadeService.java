package br.com.nexoimob.services;

import br.com.nexoimob.entities.Ocorrencia;
import br.com.nexoimob.forms.OcorrenciaForm;
import br.com.nexoimob.repositories.OcorrenciaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QualidadeService {
    private final OcorrenciaRepository ocorrencias;
    private final LeadService leads;
    private final AcessoService acesso;

    public QualidadeService(OcorrenciaRepository ocorrencias, LeadService leads,
            AcessoService acesso) {
        this.ocorrencias = ocorrencias;
        this.leads = leads;
        this.acesso = acesso;
    }

    public List<Ocorrencia> listar() {
        if (acesso.ehAdministrador()) {
            return ocorrencias.findAllByOrderByCriadaEmDesc();
        }
        return ocorrencias.findByLeadResponsavelIdOrderByCriadaEmDesc(acesso.usuarioAtual().getId());
    }

    public List<Ocorrencia> ocorrenciasDoLead(Long id) {
        leads.buscar(id);
        return ocorrencias.findByLeadId(id);
    }

    public Ocorrencia buscar(Long id) {
        Ocorrencia ocorrencia = ocorrencias.findById(id).orElseThrow();
        acesso.verificarLead(ocorrencia.getLead());
        return ocorrencia;
    }

    public void salvar(Long id, OcorrenciaForm form) {
        // Qualidade pode registrar uma ação corretiva mesmo após o encerramento.
        Ocorrencia ocorrencia;
        if (id == null) {
            ocorrencia = new Ocorrencia();
        } else {
            ocorrencia = buscar(id);
        }
        ocorrencia.setLead(leads.buscar(form.getLeadId()));
        ocorrencia.setCategoria(form.getCategoria());
        ocorrencia.setDescricao(form.getDescricao());
        ocorrencia.setAcao(form.getAcao());
        if (id == null) {
            ocorrencia.setCriadaEm(LocalDateTime.now());
        }
        ocorrencias.saveAndFlush(ocorrencia);
    }

    public void excluir(Long id) {
        ocorrencias.delete(buscar(id));
    }
}
