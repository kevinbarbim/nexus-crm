package br.com.nexoimob.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitas")
public class Visita extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "lead_id")
    private Lead lead;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "imovel_id")
    private Imovel imovel;
    @Column(name = "agendada_em",nullable = false)
    private LocalDateTime agendadaEm;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private SituacaoVisita situacao;
    @Column(nullable = false)
    private boolean virtual;
    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal km;
    @Column(length = 2000)
    private String observacoes;
    public Lead getLead() {
        return lead;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void setImovel(Imovel imovel) {
        this.imovel = imovel;
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
