package br.com.nexoimob.repositories;

import br.com.nexoimob.entities.TokenRecuperacao;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, Long> {
    // Evita o uso simultâneo do mesmo link.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TokenRecuperacao> findByTokenHash(String tokenHash);

    List<TokenRecuperacao> findByUsuarioIdAndUsadoFalse(Long usuarioId);
}
