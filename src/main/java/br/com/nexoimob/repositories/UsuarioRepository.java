package br.com.nexoimob.repositories;

import br.com.nexoimob.entities.*;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    long countByPerfilAndAtivoTrue(Perfil perfil);
    List<Usuario> findByAtivoTrueOrderByNomeAsc();
}
