package br.com.nexoimob.web;

import br.com.nexoimob.services.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PerfilController {
    private final UsuarioService usuarios;
    private final AcessoService acesso;

    public PerfilController(UsuarioService usuarios, AcessoService acesso) {
        this.usuarios = usuarios;
        this.acesso = acesso;
    }

    @GetMapping("/perfil")
    public String perfil(Model modelo) {
        modelo.addAttribute("usuario", acesso.usuarioAtual());
        modelo.addAttribute("nome", acesso.usuarioAtual().getNome());
        return "perfil";
    }

    @PostMapping("/perfil")
    public String salvar(@RequestParam String nome,
            @RequestParam(defaultValue = "") String senha, Model modelo,
            HttpServletResponse resposta) {
        try {
            usuarios.salvarPerfil(nome, senha);
            return "redirect:/perfil?salvo";
        } catch (IllegalArgumentException erro) {
            modelo.addAttribute("usuario", acesso.usuarioAtual());
            modelo.addAttribute("nome", nome);
            modelo.addAttribute("erro", erro.getMessage());
            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "perfil";
        }
    }

    // A página também recebe POSTs recusados pela segurança.
    @RequestMapping("/acesso-negado")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String acessoNegado(Model modelo) {
        modelo.addAttribute("message", "Você não tem permissão para esta área");
        return "error";
    }
}
