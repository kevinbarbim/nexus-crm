package br.com.nexoimob.repositories;

import br.com.nexoimob.entities.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropostaRepository extends JpaRepository<Proposta, Long> {
    List<Proposta> findByLeadId(Long id);
    List<Proposta> findAllByOrderByCriadaEmDesc();
    List<Proposta> findByLeadResponsavelIdOrderByCriadaEmDesc(Long id);

}
