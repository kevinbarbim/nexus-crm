package br.com.nexoimob.repositories;

import br.com.nexoimob.entities.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {
    List<Atividade> findByLeadId(Long id);
    List<Atividade> findAllByOrderByPrazoAsc();
    List<Atividade> findByLeadResponsavelIdOrderByPrazoAsc(Long id);
}
