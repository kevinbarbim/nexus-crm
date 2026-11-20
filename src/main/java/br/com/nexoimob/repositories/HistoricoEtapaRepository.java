package br.com.nexoimob.repositories;

import br.com.nexoimob.entities.HistoricoEtapa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoEtapaRepository extends JpaRepository<HistoricoEtapa, Long> {
    List<HistoricoEtapa> findByLeadIdOrderByAlteradaEmDesc(Long id);
}
