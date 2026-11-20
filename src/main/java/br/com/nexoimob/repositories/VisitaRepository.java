package br.com.nexoimob.repositories;

import br.com.nexoimob.entities.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitaRepository extends JpaRepository<Visita, Long> {
    List<Visita> findByLeadId(Long id);
    List<Visita> findAllByOrderByAgendadaEmDesc();
    List<Visita> findByLeadResponsavelIdOrderByAgendadaEmDesc(Long id);
}
