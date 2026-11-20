package br.com.nexoimob.repositories;

import br.com.nexoimob.entities.Interesse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteresseRepository extends JpaRepository<Interesse, Long> {
    List<Interesse> findByLeadId(Long leadId);

    boolean existsByLeadIdAndImovelId(Long leadId, Long imovelId);
}
