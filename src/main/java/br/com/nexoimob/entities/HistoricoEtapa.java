package br.com.nexoimob.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_etapas")
public class HistoricoEtapa extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "lead_id")
    private Lead lead;
    @Column(name = "etapa_anterior",length = 30)
    private String etapaAnterior;
    @Column(name = "etapa_nova",nullable = false,length = 30)
    private String etapaNova;
    @Column(name = "alterada_em",nullable = false)
    private LocalDateTime alteradaEm;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "autor_id")
    private Usuario autor;
    public Lead getLead() {
        return lead;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public String getEtapaAnterior() {
        return etapaAnterior;
    }

    public void setEtapaAnterior(String etapaAnterior) {
        this.etapaAnterior = etapaAnterior;
    }

    public String getEtapaNova() {
        return etapaNova;
    }

    public void setEtapaNova(String etapaNova) {
        this.etapaNova = etapaNova;
    }

    public LocalDateTime getAlteradaEm() {
        return alteradaEm;
    }

    public void setAlteradaEm(LocalDateTime alteradaEm) {
        this.alteradaEm = alteradaEm;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

}
