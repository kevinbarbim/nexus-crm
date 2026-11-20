package br.com.nexoimob.config;

import br.com.nexoimob.entities.Perfil;
import br.com.nexoimob.entities.Usuario;
import br.com.nexoimob.repositories.UsuarioRepository;
import br.com.nexoimob.services.SenhaUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Cria o administrador inicial em uma base vazia. */
@Component
public class BootstrapAdmin implements CommandLineRunner {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final String email;
    private final String senha;

    public BootstrapAdmin(UsuarioRepository usuarios, PasswordEncoder encoder,
            @Value("${app.bootstrap-email}") String email,
            @Value("${app.bootstrap-password}") String senha) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() > 0) {
            return;
        }
        if (email.isBlank()) {
            throw new IllegalStateException("Configure ADMIN_EMAIL para o primeiro administrador");
        }
        SenhaUtil.validar(senha);

        Usuario administrador = new Usuario();
        administrador.setNome("Administrador");
        administrador.setEmail(email.trim().toLowerCase());
        administrador.setSenhaHash(encoder.encode(senha));
        administrador.setPerfil(Perfil.ADMIN);
        administrador.setAtivo(true);
        usuarios.save(administrador);
    }
}
