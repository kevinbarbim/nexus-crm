package br.com.nexoimob.entities;

public enum SituacaoProposta {
    PENDENTE("Pendente"),
    ACEITA("Aceita"),
    RECUSADA("Recusada");

    private final String descricao;

    SituacaoProposta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
