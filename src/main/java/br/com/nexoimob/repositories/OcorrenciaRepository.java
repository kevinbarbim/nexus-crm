package br.com.nexoimob.repositories;

import br.com.nexoimob.entities.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    List<Ocorrencia> findByLeadId(Long id);
    List<Ocorrencia> findAllByOrderByCriadaEmDesc();
    List<Ocorrencia> findByLeadResponsavelIdOrderByCriadaEmDesc(Long id);
}
