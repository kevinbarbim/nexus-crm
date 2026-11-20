package br.com.nexoimob.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "propostas")
public class Proposta extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "lead_id")
    private Lead lead;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "imovel_id")
    private Imovel imovel;
    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private SituacaoProposta situacao;
    @Column(name = "criada_em",nullable = false)
    private LocalDateTime criadaEm;
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

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(LocalDateTime criadaEm) {
        this.criadaEm = criadaEm;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

}
