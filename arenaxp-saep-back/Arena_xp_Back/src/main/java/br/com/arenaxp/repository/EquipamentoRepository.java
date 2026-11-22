package br.com.arenaxp.repository;

import br.com.arenaxp.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    boolean existsByCodigoPatrimonioIgnoreCase(String codigoPatrimonio);

    boolean existsByCodigoPatrimonioIgnoreCaseAndIdNot(String codigoPatrimonio, Long id);

    Optional<Equipamento> findByCodigoPatrimonioIgnoreCase(String codigoPatrimonio);

    List<Equipamento> findAllByOrderByNomeAsc();

    @Query("""
           SELECT e FROM Equipamento e
           WHERE (:termo IS NULL OR :termo = ''
                  OR LOWER(e.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
                  OR LOWER(e.codigoPatrimonio) LIKE LOWER(CONCAT('%', :termo, '%')))
             AND (:categoria IS NULL OR :categoria = ''
                  OR LOWER(e.categoria) = LOWER(:categoria))
           ORDER BY e.nome ASC
           """)
    List<Equipamento> buscarComFiltros(@Param("termo") String termo,
                                       @Param("categoria") String categoria);
}
