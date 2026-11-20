package br.com.nexoimob.repositories;

import br.com.nexoimob.entities.Lead;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByResponsavelIdOrderByCriadoEmDesc(Long id);
    List<Lead> findAllByOrderByCriadoEmDesc();

}
