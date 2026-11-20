package br.com.nexoimob.entities;

public enum Origem {
    INDICACAO("Indicação"),
    PORTAL("Portal"),
    REDES_SOCIAIS("Redes sociais"),
    SITE("Site"),
    OUTRO("Outro");

    private final String descricao;

    Origem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
