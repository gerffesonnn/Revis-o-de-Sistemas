package br.com.arenaxp.controller;

import br.com.arenaxp.model.Equipamento;
import br.com.arenaxp.service.EquipamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipamentos")
public class EquipamentoRestController {

    private final EquipamentoService equipamentoService;

    public EquipamentoRestController(EquipamentoService equipamentoService) {
        this.equipamentoService = equipamentoService;
    }

    @GetMapping
    public List<Equipamento> listar(@RequestParam(required = false) String termo,
                                    @RequestParam(required = false) String categoria) {
        return equipamentoService.listarComFiltros(termo, categoria);
    }

    @GetMapping("/estoque")
    public List<Equipamento> listarEstoqueOrdenado() {
        return equipamentoService.listarOrdenadoAlfabeticamente();
    }

    @GetMapping("/resumo")
    public Map<String, Object> resumoDashboard() {
        List<Equipamento> todos = equipamentoService.listarOrdenadoAlfabeticamente();
        long criticos = todos.stream().filter(Equipamento::isEstoqueCritico).count();
        int totalUnidades = todos.stream().mapToInt(Equipamento::getQuantidadeAtual).sum();

        return Map.of(
                "totalEquipamentos", todos.size(),
                "totalUnidades", totalUnidades,
                "itensCriticos", criticos,
                "listaCriticos", todos.stream().filter(Equipamento::isEstoqueCritico).toList()
        );
    }

    @GetMapping("/{id}")
    public Equipamento buscarPorId(@PathVariable Long id) {
        return equipamentoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Equipamento> criar(@RequestBody Equipamento equipamento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipamentoService.salvar(equipamento));
    }

    @PutMapping("/{id}")
    public Equipamento atualizar(@PathVariable Long id, @RequestBody Equipamento equipamento) {
        return equipamentoService.atualizar(id, equipamento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        equipamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
