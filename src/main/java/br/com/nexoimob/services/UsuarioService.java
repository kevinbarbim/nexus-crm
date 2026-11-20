package br.com.nexoimob.services;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.UsuarioForm;
import br.com.nexoimob.repositories.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final AcessoService acesso;

    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder encoder,
            AcessoService acesso) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.acesso = acesso;
    }

    public List<Usuario> listar() {
        acesso.exigirAdministrador();
        return usuarios.findAll();
    }

    public Usuario buscar(Long id) {
        acesso.exigirAdministrador();
        return usuarios.findById(id).orElseThrow();
    }

    public List<Usuario> responsaveisAtivos() {
        acesso.exigirAdministrador();
        return usuarios.findByAtivoTrueOrderByNomeAsc();
    }

    public void salvar(Long id, UsuarioForm form) {
        acesso.exigirAdministrador();
        Usuario usuario;
        if (id == null) {
            usuario = new Usuario();
        } else {
            usuario = buscar(id);
        }
        if (id != null && id.equals(acesso.usuarioAtual().getId())) {
            throw new IllegalArgumentException("Edite seus dados em Meu perfil");
        }
        if (usuario.getPerfil() == Perfil.ADMIN && usuario.isAtivo()
                && (!form.isAtivo() || form.getPerfil() != Perfil.ADMIN)
                && usuarios.countByPerfilAndAtivoTrue(Perfil.ADMIN) <= 1) {
            throw new IllegalArgumentException("Mantenha pelo menos um administrador ativo");
        }
        boolean mudarSenha = form.getSenha() != null && !form.getSenha().isBlank();
        if (id == null || mudarSenha) {
            SenhaUtil.validar(form.getSenha());
            usuario.setSenhaHash(encoder.encode(form.getSenha()));
        }
        usuario.setNome(form.getNome().trim());
        usuario.setEmail(form.getEmail().trim().toLowerCase());
        usuario.setPerfil(form.getPerfil());
        usuario.setAtivo(form.isAtivo());
        usuarios.saveAndFlush(usuario);
    }

    public void excluir(Long id) {
        acesso.exigirAdministrador();
        if (id.equals(acesso.usuarioAtual().getId())) {
            throw new IllegalArgumentException("Não é possível excluir seu próprio acesso");
        }
        usuarios.delete(buscar(id));
        usuarios.flush();
    }

    public void salvarPerfil(String nome, String senha) {
        if (nome == null || nome.isBlank() || nome.length() > 120) {
            throw new IllegalArgumentException("Informe um nome de até 120 caracteres");
        }
        boolean mudarSenha = senha != null && !senha.isBlank();
        if (mudarSenha) {
            SenhaUtil.validar(senha);
        }
        Usuario usuario = acesso.usuarioAtual();
        usuario.setNome(nome.trim());
        if (mudarSenha) {
            usuario.setSenhaHash(encoder.encode(senha));
        }
        usuarios.saveAndFlush(usuario);
    }

    public void cadastrar(String nome, String email, String senha) {
        if (nome == null || nome.isBlank() || nome.length() > 120
                || email == null || email.length() > 180
                || !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Informe nome e e-mail válidos");
        }
        SenhaUtil.validar(senha);
        if (usuarios.findByEmailIgnoreCase(email.trim()).isPresent()) {
            throw new IllegalArgumentException("Não foi possível concluir este cadastro");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome.trim());
        usuario.setEmail(email.trim().toLowerCase());
        usuario.setSenhaHash(encoder.encode(senha));
        usuario.setPerfil(Perfil.CORRETOR);
        usuario.setAtivo(false); // Somente um administrador aprova o cadastro.
        usuarios.saveAndFlush(usuario);
    }
}
