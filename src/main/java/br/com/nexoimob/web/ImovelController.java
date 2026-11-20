package br.com.nexoimob.web;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.ImovelForm;
import br.com.nexoimob.services.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ImovelController {
    private final ImovelService imoveis;
    private final AcessoService acesso;
    private final LeadService leadService;

    public ImovelController(ImovelService imoveis, AcessoService acesso, LeadService leadService) {
        this.imoveis = imoveis;
        this.acesso = acesso;
        this.leadService = leadService;
    }

    @GetMapping("/imoveis")
    public String listar(Model modelo) {
        modelo.addAttribute("registros", imoveis.listar());
        return "imoveis/list";
    }

    @GetMapping("/imoveis/novo")
    public String novo(Model modelo) {
        ImovelForm form = new ImovelForm();
        form.setCidade("São José do Rio Preto");
        form.setTipo(TipoImovel.CASA);
        form.setFinalidade(Finalidade.VENDA);
        form.setSituacao(SituacaoImovel.DISPONIVEL);
        form.setQuartos(0);
        return formulario(modelo, form, null);
    }

    @GetMapping("/imoveis/{id}/editar")
    public String editar(@PathVariable Long id, Model modelo) {
        Imovel registro = imoveis.buscar(id);
        acesso.verificarImovel(registro);
        ImovelForm form = new ImovelForm();
        form.setTitulo(registro.getTitulo());
        form.setTipo(registro.getTipo());
        form.setFinalidade(registro.getFinalidade());
        form.setCidade(registro.getCidade());
        form.setBairro(registro.getBairro());
        form.setQuartos(registro.getQuartos());
        form.setArea(registro.getArea());
        form.setValor(registro.getValor());
        form.setSituacao(registro.getSituacao());
        form.setDescricao(registro.getDescricao());
        return formulario(modelo, form, id);
    }

    @PostMapping({"/imoveis/novo", "/imoveis/{id}/editar"})
    public String salvar(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") ImovelForm form, BindingResult erros,
            Model modelo, HttpServletResponse resposta) {
        if (erros.hasErrors()) {
            return formulario(modelo, form, id);
        }
        try {
            imoveis.salvar(id, form);
            return "redirect:/imoveis?salvo";
        } catch (IllegalArgumentException erro) {
            erros.reject("regra", erro.getMessage());
            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return formulario(modelo, form, id);
        }
    }

    @PostMapping("/imoveis/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        imoveis.excluir(id);
        return "redirect:/imoveis?salvo";
    }

    @GetMapping("/imoveis/opcoes")
    @ResponseBody
    public List<ImovelOpcao> opcoes(@RequestParam Long leadId) {
        List<ImovelOpcao> resultado = new ArrayList<>();
        Lead lead = leadService.buscar(leadId);
        for (Imovel imovel : imoveis.listarInteresses(lead)) {
            resultado.add(new ImovelOpcao(imovel.getId(), imovel.getTitulo()));
        }
        return resultado;
    }

    private String formulario(Model modelo, ImovelForm form, Long id) {
        modelo.addAttribute("form", form);
        modelo.addAttribute("titulo", id == null ? "Cadastrar imóvel" : "Editar imóvel");
        modelo.addAttribute("acao", id == null ? "/imoveis/novo" : "/imoveis/" + id + "/editar");
        modelo.addAttribute("tipos", TipoImovel.values());
        modelo.addAttribute("finalidades", Finalidade.values());
        modelo.addAttribute("situacoes", SituacaoImovel.values());
        return "imoveis/form";
    }
}
