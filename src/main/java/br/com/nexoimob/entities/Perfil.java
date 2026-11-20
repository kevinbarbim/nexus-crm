package br.com.nexoimob.entities;

public enum Perfil {
    ADMIN("Administrador"),
    CORRETOR("Corretor");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
