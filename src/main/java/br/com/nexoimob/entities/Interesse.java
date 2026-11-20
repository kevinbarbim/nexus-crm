package br.com.nexoimob.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interesses", uniqueConstraints=@UniqueConstraint(columnNames={"lead_id","imovel_id"}))
public class Interesse extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "lead_id")
    private Lead lead;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "imovel_id")
    private Imovel imovel;
    @Column(name = "criado_em",nullable = false)
    private LocalDateTime criadoEm;
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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

}
