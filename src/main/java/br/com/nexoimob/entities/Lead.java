package br.com.nexoimob.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
public class Lead extends BaseEntity {
    @Column(nullable = false,length = 120)
    private String nome;
    @Column(length = 180)
    private String email;
    @Column(nullable = false,length = 30)
    private String telefone;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private Origem origem;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private Etapa etapa;
    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal orcamento;
    @Column(length = 2000)
    private String observacoes;
    @Column(name = "motivo_perda",length = 200)
    private String motivoPerda;
    @Column(name = "criado_em",nullable = false)
    private LocalDateTime criadoEm;
    @Column(name = "primeiro_contato_em")
    private LocalDateTime primeiroContatoEm;
    @Column(name = "encerrado_em")
    private LocalDateTime encerradoEm;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "atualizado_por_id")
    private Usuario atualizadoPor;
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Origem getOrigem() {
        return origem;
    }

    public void setOrigem(Origem origem) {
        this.origem = origem;
    }

    public Etapa getEtapa() {
        return etapa;
    }

    public void setEtapa(Etapa etapa) {
        this.etapa = etapa;
    }

    public BigDecimal getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(BigDecimal orcamento) {
        this.orcamento = orcamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getMotivoPerda() {
        return motivoPerda;
    }

    public void setMotivoPerda(String motivoPerda) {
        this.motivoPerda = motivoPerda;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getPrimeiroContatoEm() {
        return primeiroContatoEm;
    }

    public void setPrimeiroContatoEm(LocalDateTime primeiroContatoEm) {
        this.primeiroContatoEm = primeiroContatoEm;
    }

    public LocalDateTime getEncerradoEm() {
        return encerradoEm;
    }

    public void setEncerradoEm(LocalDateTime encerradoEm) {
        this.encerradoEm = encerradoEm;
    }

    public Usuario getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Usuario responsavel) {
        this.responsavel = responsavel;
    }

    public Usuario getAtualizadoPor() {
        return atualizadoPor;
    }

    public void setAtualizadoPor(Usuario atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
    }

}
