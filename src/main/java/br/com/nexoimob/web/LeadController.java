package br.com.nexoimob.web;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.LeadForm;
import br.com.nexoimob.services.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class LeadController {
    private final LeadService leads;
    private final ImovelService imoveis;
    private final AgendaService agenda;
    private final NegociacaoService negociacoes;
    private final QualidadeService qualidade;
    private final UsuarioService usuarios;
    private final AcessoService acesso;

    public LeadController(LeadService leads, ImovelService imoveis, AgendaService agenda,
            NegociacaoService negociacoes, QualidadeService qualidade,
            UsuarioService usuarios, AcessoService acesso) {
        this.leads = leads;
        this.imoveis = imoveis;
        this.agenda = agenda;
        this.negociacoes = negociacoes;
        this.qualidade = qualidade;
        this.usuarios = usuarios;
        this.acesso = acesso;
    }

    @GetMapping("/leads")
    public String listar(Model modelo, @RequestParam(defaultValue = "") String q) {
        String busca = q.trim().toLowerCase(Locale.ROOT);
        List<Lead> resultado = new ArrayList<>();
        Map<String, Integer> contagem = new LinkedHashMap<>();
        for (Etapa etapa : Etapa.values()) {
            contagem.put(etapa.name(), 0);
        }
        for (Lead lead : leads.listar()) {
            if (lead.getNome().toLowerCase(Locale.ROOT).contains(busca)
                    || lead.getTelefone().contains(busca)) {
                resultado.add(lead);
                String etapa = lead.getEtapa().name();
                contagem.put(etapa, contagem.get(etapa) + 1);
            }
        }
        modelo.addAttribute("leads", resultado);
        modelo.addAttribute("etapas", Etapa.values());
        modelo.addAttribute("counts", contagem);
        modelo.addAttribute("q", q);
        return "leads";
    }

    @GetMapping("/leads/{id}")
    public String ficha(@PathVariable Long id, Model modelo) {
        Lead lead = leads.buscar(id);
        List<Etapa> etapas = new ArrayList<>();
        for (Etapa etapa : Etapa.values()) {
            if (etapa != Etapa.GANHO) {
                etapas.add(etapa);
            }
        }
        modelo.addAttribute("lead", lead);
        modelo.addAttribute("encerrado", leads.encerrado(lead));
        modelo.addAttribute("etapas", etapas);
        modelo.addAttribute("motivos", MotivoPerda.values());
        modelo.addAttribute("interesses", leads.interessesDoLead(id));
        modelo.addAttribute("imoveis", imoveis.listarDisponiveis());
        modelo.addAttribute("tarefas", agenda.atividadesDoLead(id));
        modelo.addAttribute("visitas", agenda.visitasDoLead(id));
        modelo.addAttribute("propostas", negociacoes.propostasDoLead(id));
        modelo.addAttribute("ocorrencias", qualidade.ocorrenciasDoLead(id));
        modelo.addAttribute("historico", leads.historicoDoLead(id));
        return "lead";
    }

    @GetMapping("/leads/novo")
    public String novo(Model modelo) {
        LeadForm form = new LeadForm();
        form.setOrigem(Origem.INDICACAO);
        form.setOrcamento(BigDecimal.ZERO);
        form.setResponsavelId(acesso.usuarioAtual().getId());
        return formulario(modelo, form, null);
    }

    @GetMapping("/leads/{id}/editar")
    public String editar(@PathVariable Long id, Model modelo) {
        Lead lead = leads.buscar(id);
        LeadForm form = new LeadForm();
        form.setNome(lead.getNome());
        form.setEmail(lead.getEmail());
        form.setTelefone(lead.getTelefone());
        form.setOrigem(lead.getOrigem());
        form.setOrcamento(lead.getOrcamento());
        form.setObservacoes(lead.getObservacoes());
        form.setResponsavelId(lead.getResponsavel().getId());
        return formulario(modelo, form, id);
    }

    @PostMapping({"/leads/novo", "/leads/{id}/editar"})
    public String salvar(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") LeadForm form, BindingResult erros,
            Model modelo, HttpServletResponse resposta) {
        if (erros.hasErrors()) {
            return formulario(modelo, form, id);
        }
        try {
            Lead lead = leads.salvar(id, form);
            return "redirect:/leads/" + lead.getId() + "?salvo";
        } catch (IllegalArgumentException erro) {
            erros.reject("regra", erro.getMessage());
            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return formulario(modelo, form, id);
        }
    }

    @PostMapping("/leads/{id}/etapa")
    public String mover(@PathVariable Long id, @RequestParam Etapa etapa,
            @RequestParam(required = false) String motivo) {
        leads.mover(id, etapa, motivo);
        return "redirect:/leads/" + id + "?salvo";
    }

    @PostMapping("/leads/{id}/interesses")
    public String adicionarInteresse(@PathVariable Long id, @RequestParam Long imovelId) {
        leads.adicionarInteresse(id, imovelId);
        return "redirect:/leads/" + id + "?salvo";
    }

    @PostMapping("/interesses/{id}/excluir")
    public String removerInteresse(@PathVariable Long id) {
        Long leadId = leads.removerInteresse(id);
        return "redirect:/leads/" + leadId + "?salvo";
    }

    @PostMapping("/leads/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        leads.excluir(id);
        return "redirect:/leads?salvo";
    }

    private String formulario(Model modelo, LeadForm form, Long id) {
        modelo.addAttribute("form", form);
        modelo.addAttribute("titulo", id == null ? "Novo lead" : "Editar lead");
        modelo.addAttribute("acao", id == null ? "/leads/novo" : "/leads/" + id + "/editar");
        modelo.addAttribute("origens", Origem.values());
        if (acesso.ehAdministrador()) {
            modelo.addAttribute("responsaveis", usuarios.responsaveisAtivos());
        }
        return "leads/form";
    }
}
