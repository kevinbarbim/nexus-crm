package br.com.nexoimob.web;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.services.AcessoService;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class CommonModel {
    private static final Logger log = LoggerFactory.getLogger(CommonModel.class);
    private final AcessoService acesso;

    public CommonModel(AcessoService acesso) {
        this.acesso = acesso;
    }

    @ModelAttribute
    void dadosDoMenu(Model modelo) {
        Optional<Usuario> usuario = acesso.usuarioDaSessao();
        if (usuario.isPresent() && usuario.get().isAtivo()) {
            modelo.addAttribute("actor", usuario.get());
            modelo.addAttribute("admin", usuario.get().getPerfil() == Perfil.ADMIN);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String dadosInvalidos(IllegalArgumentException erro, Model modelo) {
        return mostrarErro(modelo, erro.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String registroAusente(Model modelo) {
        return mostrarErro(modelo, "Registro não encontrado");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String acessoNegado(Model modelo) {
        return mostrarErro(modelo, "Você não tem permissão para esta operação");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String integridade(Model modelo) {
        return mostrarErro(modelo, "Registro já existente ou utilizado em outro cadastro");
    }

    @ExceptionHandler(DataAccessException.class)
    String falhaBanco(DataAccessException erro, Model modelo, HttpServletResponse resposta) {
        String mensagem = "Não foi possível acessar os dados. Tente novamente mais tarde.";
        resposta.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        Throwable causa = erro;
        while (causa != null) {
            if (causa instanceof SQLException) {
                SQLException sql = (SQLException) causa;
                // Os códigos 51000 a 51099 são regras dos scripts do projeto.
                if (sql.getErrorCode() >= 51000 && sql.getErrorCode() <= 51099) {
                    mensagem = sql.getMessage();
                    resposta.setStatus(HttpServletResponse.SC_CONFLICT);
                    break;
                }
            }
            causa = causa.getCause();
        }
        log.warn("Operação de banco não concluída", erro);
        // Evita uma nova consulta ao banco durante a falha.
        modelo.addAttribute("message", mensagem);
        return "error";
    }

    private String mostrarErro(Model modelo, String mensagem) {
        dadosDoMenu(modelo);
        modelo.addAttribute("message", mensagem);
        return "error";
    }
}
