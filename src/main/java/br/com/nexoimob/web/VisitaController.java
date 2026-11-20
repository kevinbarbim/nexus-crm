package br.com.nexoimob.web;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.VisitaForm;
import br.com.nexoimob.services.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class VisitaController {
    private final AgendaService agenda;
    private final LeadService leads;
    private final ImovelService imoveis;

    public VisitaController(AgendaService agenda, LeadService leads, ImovelService imoveis) {
        this.agenda = agenda;
        this.leads = leads;
        this.imoveis = imoveis;
    }

    @GetMapping("/visitas")
    public String listar(Model modelo) {
        modelo.addAttribute("registros", agenda.listarVisitas());
        return "visitas/list";
    }

    @GetMapping("/visitas/novo")
    public String novo(@RequestParam(required = false) Long leadId, Model modelo) {
        VisitaForm form = new VisitaForm();
        form.setSituacao(SituacaoVisita.AGENDADA);
        form.setKm(BigDecimal.ZERO);
        form.setAgendadaEm(LocalDateTime.now().plusDays(1).withSecond(0).withNano(0));
        form.setLeadId(leadId);
        return formulario(modelo, form, null);
    }

    @GetMapping("/visitas/{id}/editar")
    public String editar(@PathVariable Long id, Model modelo) {
        Visita registro = agenda.buscarVisita(id);

        VisitaForm form = new VisitaForm();
        form.setLeadId(registro.getLead().getId());
        form.setImovelId(registro.getImovel().getId());
        form.setAgendadaEm(registro.getAgendadaEm());
        form.setSituacao(registro.getSituacao());
        form.setVirtual(registro.isVirtual());
        form.setKm(registro.getKm());
        form.setObservacoes(registro.getObservacoes());
        return formulario(modelo, form, id);
    }

    @PostMapping({"/visitas/novo", "/visitas/{id}/editar"})
    public String salvar(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") VisitaForm form, BindingResult erros,
            Model modelo, HttpServletResponse resposta) {
        if (erros.hasErrors()) {
            return formulario(modelo, form, id);
        }
        try {
            agenda.salvarVisita(id, form);
            return "redirect:/visitas?salvo";
        } catch (IllegalArgumentException erro) {
            erros.reject("regra", erro.getMessage());
            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return formulario(modelo, form, id);
        }
    }

    @PostMapping("/visitas/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        agenda.excluirVisita(id);
        return "redirect:/visitas?salvo";
    }

    private String formulario(Model modelo, VisitaForm form, Long id) {
        modelo.addAttribute("form", form);
        modelo.addAttribute("titulo", id == null ? "Cadastrar visita" : "Editar visita");
        modelo.addAttribute("acao", id == null ? "/visitas/novo" : "/visitas/" + id + "/editar");
        modelo.addAttribute("leads", leads.listarAbertos());
        modelo.addAttribute("situacoes", SituacaoVisita.values());
        modelo.addAttribute("imoveis", form.getLeadId() == null ? List.of()
                : imoveis.listarInteresses(leads.buscar(form.getLeadId())));
        return "visitas/form";
    }
}
