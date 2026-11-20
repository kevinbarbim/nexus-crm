package br.com.nexoimob.entities;

public enum Finalidade {
    VENDA("Venda"),
    LOCACAO("Locação");

    private final String descricao;

    Finalidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
