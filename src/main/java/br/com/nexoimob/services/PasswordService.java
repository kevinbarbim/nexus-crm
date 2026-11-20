package br.com.nexoimob.services;

import br.com.nexoimob.entities.TokenRecuperacao;
import br.com.nexoimob.entities.Usuario;
import br.com.nexoimob.repositories.TokenRecuperacaoRepository;
import br.com.nexoimob.repositories.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PasswordService {
    private final UsuarioRepository usuarios;
    private final TokenRecuperacaoRepository tokens;
    private final PasswordEncoder encoder;
    private final ObjectProvider<JavaMailSender> remetente;
    private final String enderecoBase;
    private final String emailRemetente;

    public PasswordService(UsuarioRepository usuarios, TokenRecuperacaoRepository tokens,
            PasswordEncoder encoder, ObjectProvider<JavaMailSender> remetente,
            @Value("${app.base-url}") String enderecoBase,
            @Value("${app.mail-from}") String emailRemetente) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.encoder = encoder;
        this.remetente = remetente;
        this.enderecoBase = enderecoBase;
        this.emailRemetente = emailRemetente;
    }

    // Somente o hash do token é armazenado no banco.
    public static String hash(String valor) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException erro) {
            throw new IllegalStateException(erro);
        }
    }

    public void solicitar(String email) {
        if (email == null || email.length() > 180
                || !email.trim().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Informe um e-mail válido");
        }
        Optional<Usuario> encontrado = usuarios.findByEmailIgnoreCase(email.trim());
        if (encontrado.isEmpty() || !encontrado.get().isAtivo()) {
            return; // Não revela se o e-mail está cadastrado.
        }
        String tokenOriginal = UUID.randomUUID().toString() + UUID.randomUUID();
        TokenRecuperacao token = new TokenRecuperacao();
        token.setUsuario(encontrado.get());
        token.setTokenHash(hash(tokenOriginal));
        token.setExpiraEm(LocalDateTime.now().plusMinutes(15));
        tokens.save(token);
        String link = enderecoBase + "/redefinir?token=" + tokenOriginal;
        JavaMailSender emailSender = remetente.getIfAvailable();
        if (emailSender == null) {
            throw new IllegalArgumentException("Recuperação por e-mail ainda não configurada pelo administrador");
        }
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(emailRemetente);
        mensagem.setTo(encontrado.get().getEmail());
        mensagem.setSubject("Redefinir senha do Nexus");
        mensagem.setText("Use este link em até 15 minutos: " + link);
        emailSender.send(mensagem);
    }

    public void redefinir(String tokenOriginal, String senha) {
        SenhaUtil.validar(senha);
        Optional<TokenRecuperacao> encontrado = tokens.findByTokenHash(hash(tokenOriginal));
        if (encontrado.isEmpty()) {
            throw new IllegalArgumentException("Link inválido ou expirado");
        }
        TokenRecuperacao token = encontrado.get();
        if (token.isUsado() || token.getExpiraEm().isBefore(LocalDateTime.now())
                || !token.getUsuario().isAtivo()) {
            throw new IllegalArgumentException("Link inválido ou expirado");
        }
        Usuario usuario = token.getUsuario();
        usuario.setSenhaHash(encoder.encode(senha));
        usuarios.save(usuario);
        token.setUsado(true);
        tokens.save(token);
        // Invalida os outros links após a troca de senha.
        for (TokenRecuperacao pendente : tokens.findByUsuarioIdAndUsadoFalse(usuario.getId())) {
            pendente.setUsado(true);
            tokens.save(pendente);
        }
    }
}
