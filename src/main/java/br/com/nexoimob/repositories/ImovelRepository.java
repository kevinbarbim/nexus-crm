package br.com.nexoimob.repositories;

import br.com.nexoimob.entities.Imovel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImovelRepository extends JpaRepository<Imovel, Long> {
    List<Imovel> findAllByOrderByIdDesc();

}
