package br.com.nexoimob.forms;

import br.com.nexoimob.entities.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class VisitaForm {
    @NotNull
    private Long leadId;
    @NotNull
    private Long imovelId;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime agendadaEm;
    @NotNull
    private SituacaoVisita situacao;

    private boolean virtual;
    @NotNull @DecimalMin("0")
    @Digits(integer = 8, fraction = 2, message = "Use até 8 dígitos inteiros e 2 casas decimais.")
    private BigDecimal km;
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

    public LocalDateTime getAgendadaEm() {
        return agendadaEm;
    }

    public void setAgendadaEm(LocalDateTime agendadaEm) {
        this.agendadaEm = agendadaEm;
    }

    public SituacaoVisita getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoVisita situacao) {
        this.situacao = situacao;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public BigDecimal getKm() {
        return km;
    }

    public void setKm(BigDecimal km) {
        this.km = km;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

}
