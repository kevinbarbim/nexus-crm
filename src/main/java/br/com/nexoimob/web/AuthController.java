package br.com.nexoimob.web;

import br.com.nexoimob.services.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UsuarioService usuarios;
    private final PasswordService passwords;

    public AuthController(UsuarioService usuarios, PasswordService passwords) {
        this.usuarios = usuarios;
        this.passwords = passwords;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model modelo) {
        modelo.addAttribute("nome", "");
        modelo.addAttribute("email", "");
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@RequestParam String nome, @RequestParam String email,
            @RequestParam String senha, Model modelo, HttpServletResponse resposta) {
        try {
            usuarios.cadastrar(nome, email, senha);
            return "redirect:/login?cadastro";
        } catch (IllegalArgumentException erro) {
            modelo.addAttribute("erro", erro.getMessage());
        } catch (DataIntegrityViolationException erro) {
            modelo.addAttribute("erro", "Não foi possível concluir este cadastro");
        }
        // Preserva nome e e-mail, sem reapresentar a senha.
        modelo.addAttribute("nome", nome);
        modelo.addAttribute("email", email);
        resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "cadastro";
    }

    @GetMapping("/recuperar")
    public String recuperar(Model modelo) {
        modelo.addAttribute("email", "");
        return "recuperar";
    }

    @PostMapping("/recuperar")
    public String solicitar(@RequestParam String email, Model modelo,
            HttpServletResponse resposta) {
        try {
            passwords.solicitar(email);
            return "redirect:/recuperar?enviado";
        } catch (IllegalArgumentException erro) {
            modelo.addAttribute("erro", erro.getMessage());
        } catch (MailException erro) {
            log.warn("Falha no envio das instruções de recuperação de senha", erro);
            modelo.addAttribute("erro", "Não foi possível enviar as instruções. Tente novamente mais tarde.");
        }
        modelo.addAttribute("email", email);
        resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "recuperar";
    }

    @GetMapping("/redefinir")
    public String redefinir(@RequestParam String token, Model modelo) {
        modelo.addAttribute("token", token);
        return "redefinir";
    }

    @PostMapping("/redefinir")
    public String redefinir(@RequestParam String token, @RequestParam String senha,
            Model modelo, HttpServletResponse resposta) {
        try {
            passwords.redefinir(token, senha);
            return "redirect:/login?redefinida";
        } catch (IllegalArgumentException erro) {
            modelo.addAttribute("erro", erro.getMessage());
            modelo.addAttribute("token", token);
            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "redefinir";
        }
    }
}
