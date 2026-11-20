package br.com.nexoimob.entities;

public enum Categoria {
    ATRASO_RETORNO("Atraso no retorno"),
    INFORMACAO_INCOMPLETA("Informação incompleta"),
    VISITA_CANCELADA("Visita cancelada"),
    FALHA_DOCUMENTACAO("Falha na documentação"),
    OUTRO("Outro");

    private final String descricao;

    Categoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
