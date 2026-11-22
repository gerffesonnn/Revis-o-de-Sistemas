package br.com.arenaxp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "equipamentos")
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_patrimonio", nullable = false, unique = true, length = 30)
    private String codigoPatrimonio;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(name = "especificacao_tecnica", nullable = false, length = 255)
    private String especificacaoTecnica;

    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo;

    @Transient
    public boolean isEstoqueCritico() {
        if (quantidadeAtual == null || estoqueMinimo == null) {
            return false;
        }
        return quantidadeAtual <= estoqueMinimo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoPatrimonio() { return codigoPatrimonio; }
    public void setCodigoPatrimonio(String codigoPatrimonio) { this.codigoPatrimonio = codigoPatrimonio; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEspecificacaoTecnica() { return especificacaoTecnica; }
    public void setEspecificacaoTecnica(String especificacaoTecnica) { this.especificacaoTecnica = especificacaoTecnica; }

    public Integer getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(Integer quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }

    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
}
