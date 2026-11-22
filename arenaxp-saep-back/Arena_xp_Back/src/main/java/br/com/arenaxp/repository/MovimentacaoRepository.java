package br.com.arenaxp.repository;

import br.com.arenaxp.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findAllByOrderByDataHoraDescIdDesc();

    boolean existsByEquipamentoId(Long equipamentoId);
}
