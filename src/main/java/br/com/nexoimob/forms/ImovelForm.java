package br.com.nexoimob.forms;

import br.com.nexoimob.entities.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ImovelForm {
    @NotBlank @Size(max = 160)
    private String titulo;
    @NotNull
    private TipoImovel tipo;
    @NotNull
    private Finalidade finalidade;
    @NotBlank @Size(max = 100)
    private String cidade;
    @NotBlank @Size(max = 100)
    private String bairro;
    @NotNull @Min(0)
    @Max(99)
    private Integer quartos;
    @NotNull @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2, message = "Use até 10 dígitos inteiros e 2 casas decimais.")
    private BigDecimal area;
    @NotNull @DecimalMin("0.01")
    @Digits(integer = 17, fraction = 2, message = "Use até 17 dígitos inteiros e 2 casas decimais.")
    private BigDecimal valor;
    @NotNull
    private SituacaoImovel situacao;
    @Size(max = 2000)
    private String descricao;
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

}
