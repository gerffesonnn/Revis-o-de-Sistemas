package br.com.arenaxp.service;

import br.com.arenaxp.exception.RegraNegocioException;
import br.com.arenaxp.model.Equipamento;
import br.com.arenaxp.repository.EquipamentoRepository;
import br.com.arenaxp.repository.MovimentacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public EquipamentoService(EquipamentoRepository equipamentoRepository,
                              MovimentacaoRepository movimentacaoRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<Equipamento> listarComFiltros(String termo, String categoria) {
        return equipamentoRepository.buscarComFiltros(
                termo != null ? termo.trim() : null,
                categoria != null ? categoria.trim() : null
        );
    }

    public List<Equipamento> listarOrdenadoAlfabeticamente() {
        return equipamentoRepository.findAllByOrderByNomeAsc();
    }

    public Equipamento buscarPorId(Long id) {
        return equipamentoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Equipamento não encontrado."));
    }

    @Transactional
    public Equipamento salvar(Equipamento dados) {
        validarCampos(dados);
        String patrimonio = dados.getCodigoPatrimonio().trim().toUpperCase();

        if (equipamentoRepository.existsByCodigoPatrimonioIgnoreCase(patrimonio)) {
            throw new RegraNegocioException("Já existe um equipamento cadastrado com o patrimônio " + patrimonio + ".");
        }

        dados.setCodigoPatrimonio(patrimonio);
        dados.setNome(dados.getNome().trim());
        dados.setCategoria(dados.getCategoria().trim());
        dados.setEspecificacaoTecnica(dados.getEspecificacaoTecnica().trim());

        return equipamentoRepository.save(dados);
    }

    @Transactional
    public Equipamento atualizar(Long id, Equipamento dados) {
        Equipamento existente = buscarPorId(id);
        validarCampos(dados);
        String patrimonio = dados.getCodigoPatrimonio().trim().toUpperCase();

        if (equipamentoRepository.existsByCodigoPatrimonioIgnoreCaseAndIdNot(patrimonio, id)) {
            throw new RegraNegocioException("Outro equipamento já utiliza o patrimônio " + patrimonio + ".");
        }

        existente.setCodigoPatrimonio(patrimonio);
        existente.setNome(dados.getNome().trim());
        existente.setCategoria(dados.getCategoria().trim());
        existente.setEspecificacaoTecnica(dados.getEspecificacaoTecnica().trim());
        existente.setQuantidadeAtual(dados.getQuantidadeAtual());
        existente.setEstoqueMinimo(dados.getEstoqueMinimo());

        return equipamentoRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Equipamento existente = buscarPorId(id);

        if (movimentacaoRepository.existsByEquipamentoId(id)) {
            throw new RegraNegocioException(
                    "Não é possível excluir '" + existente.getNome() + "' pois existem movimentações de estoque vinculadas a este item."
            );
        }

        equipamentoRepository.delete(existente);
    }

    private void validarCampos(Equipamento e) {
        if (e.getCodigoPatrimonio() == null || e.getCodigoPatrimonio().isBlank()) {
            throw new RegraNegocioException("O código de patrimônio é obrigatório.");
        }
        if (e.getNome() == null || e.getNome().isBlank()) {
            throw new RegraNegocioException("O nome do equipamento é obrigatório.");
        }
        if (e.getCategoria() == null || e.getCategoria().isBlank()) {
            throw new RegraNegocioException("A categoria é obrigatória.");
        }
        if (e.getEspecificacaoTecnica() == null || e.getEspecificacaoTecnica().isBlank()) {
            throw new RegraNegocioException("A especificação técnica é obrigatória.");
        }
        if (e.getQuantidadeAtual() == null || e.getQuantidadeAtual() < 0) {
            throw new RegraNegocioException("A quantidade atual deve ser maior ou igual a zero.");
        }
        if (e.getEstoqueMinimo() == null || e.getEstoqueMinimo() < 0) {
            throw new RegraNegocioException("O estoque mínimo deve ser maior ou igual a zero.");
        }
    }
}
