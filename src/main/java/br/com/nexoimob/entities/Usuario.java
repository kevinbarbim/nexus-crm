package br.com.nexoimob.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario extends BaseEntity {
    @Column(nullable = false,length = 120)
    private String nome;
    @Column(nullable = false,unique=true,length = 180)
    private String email;
    @Column(name = "senha_hash",nullable = false,length = 100)
    private String senhaHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private Perfil perfil;
    @Column(nullable = false)
    private boolean ativo;
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

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

}
