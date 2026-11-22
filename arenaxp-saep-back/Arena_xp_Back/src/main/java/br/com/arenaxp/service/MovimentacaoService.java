package br.com.arenaxp.service;

import br.com.arenaxp.exception.RegraNegocioException;
import br.com.arenaxp.model.Equipamento;
import br.com.arenaxp.model.Movimentacao;
import br.com.arenaxp.model.Usuario;
import br.com.arenaxp.repository.EquipamentoRepository;
import br.com.arenaxp.repository.MovimentacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final AuthService authService;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository,
                               EquipamentoRepository equipamentoRepository,
                               AuthService authService) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.authService = authService;
    }

    public List<Movimentacao> listarHistorico() {
        return movimentacaoRepository.findAllByOrderByDataHoraDescIdDesc();
    }

    @Transactional
    public Movimentacao registrar(Long equipamentoId,
                                  String tipoMovimentacao,
                                  Integer quantidade,
                                  String dataHoraTexto,
                                  String observacaoEvento,
                                  Long usuarioLogadoId) {

        if (equipamentoId == null) {
            throw new RegraNegocioException("Selecione um equipamento.");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new RegraNegocioException("A quantidade movimentada deve ser maior que zero.");
        }

        String tipo = tipoMovimentacao != null ? tipoMovimentacao.trim().toUpperCase() : "";
        if (!"ENTRADA".equals(tipo) && !"SAIDA".equals(tipo)) {
            throw new RegraNegocioException("Tipo de movimentação inválido. Utilize ENTRADA ou SAIDA.");
        }

        Equipamento equipamento = equipamentoRepository.findById(equipamentoId)
                .orElseThrow(() -> new RegraNegocioException("Equipamento não encontrado."));

        Usuario tecnico = authService.buscarPorId(usuarioLogadoId);

        if ("SAIDA".equals(tipo)) {
            if (quantidade > equipamento.getQuantidadeAtual()) {
                throw new RegraNegocioException(
                        "Saldo insuficiente para '" + equipamento.getNome() + "'. Disponível em estoque: "
                                + equipamento.getQuantidadeAtual() + " unidade(s)."
                );
            }
            equipamento.setQuantidadeAtual(equipamento.getQuantidadeAtual() - quantidade);
        } else {
            equipamento.setQuantidadeAtual(equipamento.getQuantidadeAtual() + quantidade);
        }

        equipamentoRepository.save(equipamento);

        LocalDateTime dataHora = (dataHoraTexto != null && !dataHoraTexto.isBlank())
                ? LocalDateTime.parse(dataHoraTexto)
                : LocalDateTime.now();

        Movimentacao mov = new Movimentacao();
        mov.setEquipamento(equipamento);
        mov.setUsuario(tecnico);
        mov.setTipoMovimentacao(tipo);
        mov.setQuantidade(quantidade);
        mov.setDataHora(dataHora);
        mov.setObservacaoEvento(
                (observacaoEvento != null && !observacaoEvento.isBlank()) ? observacaoEvento.trim() : null
        );

        return movimentacaoRepository.save(mov);
    }
}
