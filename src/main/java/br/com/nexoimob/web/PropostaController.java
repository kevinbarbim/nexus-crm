package br.com.nexoimob.web;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.PropostaForm;
import br.com.nexoimob.services.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class PropostaController {
    private final NegociacaoService negociacoes;
    private final LeadService leads;
    private final ImovelService imoveis;

    public PropostaController(NegociacaoService negociacoes, LeadService leads, ImovelService imoveis) {
        this.negociacoes = negociacoes;
        this.leads = leads;
        this.imoveis = imoveis;
    }

    @GetMapping("/propostas")
    public String listar(Model modelo) {
        modelo.addAttribute("registros", negociacoes.listar());
        return "propostas/list";
    }

    @GetMapping("/propostas/novo")
    public String novo(@RequestParam(required = false) Long leadId, Model modelo) {
        PropostaForm form = new PropostaForm();
        form.setSituacao(SituacaoProposta.PENDENTE);
        form.setLeadId(leadId);
        return formulario(modelo, form, null);
    }

    @GetMapping("/propostas/{id}/editar")
    public String editar(@PathVariable Long id, Model modelo) {
        Proposta registro = negociacoes.buscarParaEditar(id);

        PropostaForm form = new PropostaForm();
        form.setLeadId(registro.getLead().getId());
        form.setImovelId(registro.getImovel().getId());
        form.setValor(registro.getValor());
        form.setSituacao(registro.getSituacao());
        form.setObservacoes(registro.getObservacoes());
        return formulario(modelo, form, id);
    }

    @PostMapping({"/propostas/novo", "/propostas/{id}/editar"})
    public String salvar(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") PropostaForm form, BindingResult erros,
            Model modelo, HttpServletResponse resposta) {
        if (erros.hasErrors()) {
            return formulario(modelo, form, id);
        }
        try {
            Long leadId = negociacoes.salvar(id, form);
            return "redirect:/leads/" + leadId + "?salvo";
        } catch (IllegalArgumentException erro) {
            erros.reject("regra", erro.getMessage());
            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return formulario(modelo, form, id);
        }
    }

    @PostMapping("/propostas/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        negociacoes.excluir(id);
        return "redirect:/propostas?salvo";
    }

    @PostMapping("/propostas/{id}/aceitar")
    public String aceitar(@PathVariable Long id) {
        Long leadId = negociacoes.aceitar(id);
        return "redirect:/leads/" + leadId + "?salvo";
    }

    private String formulario(Model modelo, PropostaForm form, Long id) {
        modelo.addAttribute("form", form);
        modelo.addAttribute("titulo", id == null ? "Cadastrar proposta" : "Editar proposta");
        modelo.addAttribute("acao", id == null ? "/propostas/novo" : "/propostas/" + id + "/editar");
        modelo.addAttribute("leads", leads.listarAbertos());
        modelo.addAttribute("situacoes", new SituacaoProposta[] {
                SituacaoProposta.PENDENTE, SituacaoProposta.RECUSADA});
        modelo.addAttribute("imoveis", form.getLeadId() == null ? List.of()
                : imoveis.listarInteresses(leads.buscar(form.getLeadId())));
        return "propostas/form";
    }
}
