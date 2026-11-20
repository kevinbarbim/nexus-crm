package br.com.nexoimob.entities;

public enum SituacaoImovel {
    DISPONIVEL("Disponível"),
    RESERVADO("Reservado"),
    NEGOCIADO("Negociado"),
    INATIVO("Inativo");

    private final String descricao;

    SituacaoImovel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
