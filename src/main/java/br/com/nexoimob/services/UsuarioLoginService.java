package br.com.nexoimob.services;

import br.com.nexoimob.entities.Usuario;
import br.com.nexoimob.repositories.UsuarioRepository;
import java.util.Optional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioLoginService implements UserDetailsService {
    private final UsuarioRepository usuarios;

    public UsuarioLoginService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<Usuario> encontrado = usuarios.findByEmailIgnoreCase(email.trim());
        if (encontrado.isEmpty()) {
            throw new UsernameNotFoundException("Acesso inválido");
        }
        Usuario usuario = encontrado.get();
        // O e-mail pode mudar. O ID continua identificando a sessão.
        return User.withUsername(usuario.getId().toString())
                .password(usuario.getSenhaHash())
                .roles(usuario.getPerfil().name())
                .disabled(!usuario.isAtivo())
                .build();
    }
}
