package br.com.arenaxp.controller;

import br.com.arenaxp.config.AuthInterceptor;
import br.com.arenaxp.model.Movimentacao;
import br.com.arenaxp.service.MovimentacaoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoRestController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoRestController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping
    public List<Movimentacao> listarHistorico() {
        return movimentacaoService.listarHistorico();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Map<String, Object> payload,
                                                         HttpSession session) {
        Long usuarioId = (Long) session.getAttribute(AuthInterceptor.SESSAO_USUARIO_ID);

        Long equipamentoId = payload.get("equipamentoId") != null
                ? Long.valueOf(payload.get("equipamentoId").toString()) : null;
        String tipo = payload.get("tipoMovimentacao") != null
                ? payload.get("tipoMovimentacao").toString() : null;
        Integer quantidade = payload.get("quantidade") != null
                ? Integer.valueOf(payload.get("quantidade").toString()) : null;
        String dataHora = payload.get("dataHora") != null
                ? payload.get("dataHora").toString() : null;
        String observacao = payload.get("observacaoEvento") != null
                ? payload.get("observacaoEvento").toString() : null;

        Movimentacao salva = movimentacaoService.registrar(
                equipamentoId, tipo, quantidade, dataHora, observacao, usuarioId
        );

        boolean alertaCritico = salva.getEquipamento().isEstoqueCritico();

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "movimentacao", salva,
                "alertaCritico", alertaCritico,
                "mensagem", alertaCritico
                        ? "ESTOQUE CRÍTICO / ABAIXO DO MÍNIMO: " + salva.getEquipamento().getNome()
                          + " está com " + salva.getEquipamento().getQuantidadeAtual() + " unidade(s)."
                        : "Movimentação registrada com sucesso!"
        ));
    }
}
