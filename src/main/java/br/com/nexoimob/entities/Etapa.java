package br.com.nexoimob.entities;

public enum Etapa {
    NOVO("Novo"),
    CONTATADO("Contatado"),
    QUALIFICADO("Qualificado"),
    VISITA("Visita"),
    PROPOSTA("Proposta"),
    GANHO("Ganho"),
    PERDIDO("Perdido");

    private final String descricao;

    Etapa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
