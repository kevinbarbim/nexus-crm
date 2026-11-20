package br.com.nexoimob.web;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.UsuarioForm;
import br.com.nexoimob.services.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {
    private final UsuarioService usuarios;

    public UsuarioController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @GetMapping("/usuarios")
    public String listar(Model modelo) {
        modelo.addAttribute("registros", usuarios.listar());
        return "usuarios/list";
    }

    @GetMapping("/usuarios/novo")
    public String novo(Model modelo) {
        UsuarioForm form = new UsuarioForm();
        form.setPerfil(Perfil.CORRETOR);
        return formulario(modelo, form, null);
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editar(@PathVariable Long id, Model modelo) {
        Usuario registro = usuarios.buscar(id);

        UsuarioForm form = new UsuarioForm();
        form.setNome(registro.getNome());
        form.setEmail(registro.getEmail());
        form.setPerfil(registro.getPerfil());
        form.setAtivo(registro.isAtivo());
        return formulario(modelo, form, id);
    }

    @PostMapping({"/usuarios/novo", "/usuarios/{id}/editar"})
    public String salvar(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") UsuarioForm form, BindingResult erros,
            Model modelo, HttpServletResponse resposta) {
        if (erros.hasErrors()) {
            return formulario(modelo, form, id);
        }
        try {
            usuarios.salvar(id, form);
            return "redirect:/usuarios?salvo";
        } catch (IllegalArgumentException erro) {
            erros.reject("regra", erro.getMessage());
            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return formulario(modelo, form, id);
        }
    }

    @PostMapping("/usuarios/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        usuarios.excluir(id);
        return "redirect:/usuarios?salvo";
    }

    private String formulario(Model modelo, UsuarioForm form, Long id) {
        modelo.addAttribute("form", form);
        modelo.addAttribute("titulo", id == null ? "Cadastrar usuário" : "Editar usuário");
        modelo.addAttribute("acao", id == null ? "/usuarios/novo" : "/usuarios/" + id + "/editar");
        modelo.addAttribute("perfis", Perfil.values());
        return "usuarios/form";
    }
}
