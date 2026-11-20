package br.com.nexoimob.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "imoveis")
public class Imovel extends BaseEntity {
    @Column(nullable = false,length = 160)
    private String titulo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private TipoImovel tipo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private Finalidade finalidade;
    @Column(nullable = false,length = 100)
    private String cidade;
    @Column(nullable = false,length = 100)
    private String bairro;
    @Column(nullable = false)
    private Integer quartos;
    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal area;
    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private SituacaoImovel situacao;
    @Column(length = 2000)
    private String descricao;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "criado_por_id")
    private Usuario criadoPor;
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public TipoImovel getTipo() {
        return tipo;
    }

    public void setTipo(TipoImovel tipo) {
        this.tipo = tipo;
    }

    public Finalidade getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(Finalidade finalidade) {
        this.finalidade = finalidade;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Integer getQuartos() {
        return quartos;
    }

    public void setQuartos(Integer quartos) {
        this.quartos = quartos;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public SituacaoImovel getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoImovel situacao) {
        this.situacao = situacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Usuario getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(Usuario criadoPor) {
        this.criadoPor = criadoPor;
    }

}
