package br.com.nexoimob.forms;

import br.com.nexoimob.entities.*;
import jakarta.validation.constraints.*;

public class UsuarioForm {
    @NotBlank @Size(max = 120)
    private String nome;
    @NotBlank @Email @Size(max = 180)
    private String email;
    @Size(max = 72)
    private String senha;
    @NotNull
    private Perfil perfil;

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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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
