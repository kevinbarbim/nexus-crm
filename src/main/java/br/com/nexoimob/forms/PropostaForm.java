package br.com.nexoimob.forms;

import br.com.nexoimob.entities.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class PropostaForm {
    @NotNull
    private Long leadId;
    @NotNull
    private Long imovelId;
    @NotNull @DecimalMin("0.01")
    @Digits(integer = 17, fraction = 2, message = "Use até 17 dígitos inteiros e 2 casas decimais.")
    private BigDecimal valor;
    @NotNull
    private SituacaoProposta situacao;
    @Size(max = 2000)
    private String observacoes;
    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public Long getImovelId() {
        return imovelId;
    }

    public void setImovelId(Long imovelId) {
        this.imovelId = imovelId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public SituacaoProposta getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProposta situacao) {
        this.situacao = situacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

}
