package br.com.nexoimob.web;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.OcorrenciaForm;
import br.com.nexoimob.services.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class QualidadeController {
    private final QualidadeService qualidade;
    private final LeadService leads;

    public QualidadeController(QualidadeService qualidade, LeadService leads) {
        this.qualidade = qualidade;
        this.leads = leads;
    }

    @GetMapping("/qualidade")
    public String listar(Model modelo) {
        modelo.addAttribute("registros", qualidade.listar());
        return "qualidade/list";
    }

    @GetMapping("/qualidade/novo")
    public String novo(@RequestParam(required = false) Long leadId, Model modelo) {
        OcorrenciaForm form = new OcorrenciaForm();
        form.setCategoria(Categoria.ATRASO_RETORNO);
        form.setLeadId(leadId);
        return formulario(modelo, form, null);
    }

    @GetMapping("/qualidade/{id}/editar")
    public String editar(@PathVariable Long id, Model modelo) {
        Ocorrencia registro = qualidade.buscar(id);

        OcorrenciaForm form = new OcorrenciaForm();
        form.setLeadId(registro.getLead().getId());
        form.setCategoria(registro.getCategoria());
        form.setDescricao(registro.getDescricao());
        form.setAcao(registro.getAcao());
        return formulario(modelo, form, id);
    }

    @PostMapping({"/qualidade/novo", "/qualidade/{id}/editar"})
    public String salvar(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") OcorrenciaForm form, BindingResult erros,
            Model modelo, HttpServletResponse resposta) {
        if (erros.hasErrors()) {
            return formulario(modelo, form, id);
        }
        try {
            qualidade.salvar(id, form);
            return "redirect:/qualidade?salvo";
        } catch (IllegalArgumentException erro) {
            erros.reject("regra", erro.getMessage());
            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return formulario(modelo, form, id);
        }
    }

    @PostMapping("/qualidade/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        qualidade.excluir(id);
        return "redirect:/qualidade?salvo";
    }

    private String formulario(Model modelo, OcorrenciaForm form, Long id) {
        modelo.addAttribute("form", form);
        modelo.addAttribute("titulo", id == null ? "Cadastrar ocorrência" : "Editar ocorrência");
        modelo.addAttribute("acao", id == null ? "/qualidade/novo" : "/qualidade/" + id + "/editar");
        modelo.addAttribute("leads", leads.listar());
        modelo.addAttribute("categorias", Categoria.values());
        return "qualidade/form";
    }
}
