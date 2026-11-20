package br.com.nexoimob.services;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.repositories.UsuarioRepository;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AcessoService {
    private final UsuarioRepository usuarios;

    public AcessoService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public Optional<Usuario> usuarioDaSessao() {
        Authentication login = SecurityContextHolder.getContext().getAuthentication();
        if (login == null || !login.isAuthenticated()
                || login instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        try {
            return usuarios.findById(Long.valueOf(login.getName()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Usuario usuarioAtual() {
        Optional<Usuario> encontrado = usuarioDaSessao();
        if (encontrado.isEmpty()) {
            throw new AccessDeniedException("Entre novamente no sistema");
        }
        Usuario usuario = encontrado.get();
        if (!usuario.isAtivo()) {
            throw new AccessDeniedException("Seu acesso está desativado");
        }
        return usuario;
    }

    public boolean ehAdministrador() {
        return usuarioAtual().getPerfil() == Perfil.ADMIN;
    }

    public void exigirAdministrador() {
        if (!ehAdministrador()) {
            throw new AccessDeniedException("Esta operação exige um administrador");
        }
    }

    public void verificarLead(Lead lead) {
        Usuario usuario = usuarioAtual();
        if (usuario.getPerfil() != Perfil.ADMIN
                && !lead.getResponsavel().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Acesso restrito ao corretor responsável");
        }
    }

    public void verificarImovel(Imovel imovel) {
        Usuario usuario = usuarioAtual();
        if (usuario.getPerfil() != Perfil.ADMIN
                && !imovel.getCriadoPor().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Somente o autor ou administrador pode editar");
        }
    }
}
