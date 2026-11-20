package br.com.nexoimob.config;

import br.com.nexoimob.entities.Usuario;
import br.com.nexoimob.repositories.UsuarioRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

/** Encerra sessões de usuários desativados ou com perfil alterado. */
public class VerificarSessaoFilter extends OncePerRequestFilter {
    private final UsuarioRepository usuarios;

    public VerificarSessaoFilter(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest pedido, HttpServletResponse resposta,
            FilterChain proximo) throws ServletException, IOException {
        Authentication login = SecurityContextHolder.getContext().getAuthentication();
        if (login != null && login.isAuthenticated()
                && !(login instanceof AnonymousAuthenticationToken)) {
            Usuario usuario = procurarUsuario(login.getName());
            if (!sessaoValida(login, usuario)) {
                new SecurityContextLogoutHandler().logout(pedido, resposta, login);
                resposta.sendRedirect(pedido.getContextPath() + "/login?acessoAlterado");
                return;
            }
        }
        proximo.doFilter(pedido, resposta);
    }

    private Usuario procurarUsuario(String id) {
        try {
            return usuarios.findById(Long.valueOf(id)).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean sessaoValida(Authentication login, Usuario usuario) {
        if (usuario == null || !usuario.isAtivo()) {
            return false;
        }
        SimpleGrantedAuthority perfilAtual =
                new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name());
        return login.getAuthorities().contains(perfilAtual);
    }
}
