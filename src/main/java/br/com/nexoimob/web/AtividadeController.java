package br.com.nexoimob.web;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.AtividadeForm;
import br.com.nexoimob.services.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AtividadeController {
    private final AgendaService agenda;
    private final LeadService leads;

    public AtividadeController(AgendaService agenda, LeadService leads) {
        this.agenda = agenda;
        this.leads = leads;
    }

    @GetMapping("/atividades")
    public String listar(Model modelo) {
        modelo.addAttribute("registros", agenda.listarAtividades());
        return "atividades/list";
    }

    @GetMapping("/atividades/novo")
    public String novo(@RequestParam(required = false) Long leadId, Model modelo) {
        AtividadeForm form = new AtividadeForm();
        form.setTipo(TipoAtividade.LIGACAO);
        form.setPrazo(LocalDateTime.now().plusDays(1).withSecond(0).withNano(0));
        form.setLeadId(leadId);
        return formulario(modelo, form, null);
    }

    @GetMapping("/atividades/{id}/editar")
    public String editar(@PathVariable Long id, Model modelo) {
        Atividade registro = agenda.buscarAtividade(id);

        AtividadeForm form = new AtividadeForm();
        form.setLeadId(registro.getLead().getId());
        form.setTitulo(registro.getTitulo());
        form.setTipo(registro.getTipo());
        form.setPrazo(registro.getPrazo());
        form.setConcluida(registro.getConcluidaEm() != null);
        form.setObservacoes(registro.getObservacoes());
        return formulario(modelo, form, id);
    }

    @PostMapping({"/atividades/novo", "/atividades/{id}/editar"})
    public String salvar(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") AtividadeForm form, BindingResult erros,
            Model modelo, HttpServletResponse resposta) {
        if (erros.hasErrors()) {
            return formulario(modelo, form, id);
        }
        try {
            agenda.salvarAtividade(id, form);
            return "redirect:/atividades?salvo";
        } catch (IllegalArgumentException erro) {
            erros.reject("regra", erro.getMessage());
            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return formulario(modelo, form, id);
        }
    }

    @PostMapping("/atividades/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        agenda.excluirAtividade(id);
        return "redirect:/atividades?salvo";
    }

    private String formulario(Model modelo, AtividadeForm form, Long id) {
        modelo.addAttribute("form", form);
        modelo.addAttribute("titulo", id == null ? "Cadastrar atividade" : "Editar atividade");
        modelo.addAttribute("acao", id == null ? "/atividades/novo" : "/atividades/" + id + "/editar");
        modelo.addAttribute("leads", leads.listarAbertos());
        modelo.addAttribute("tipos", TipoAtividade.values());
        return "atividades/form";
    }
}
