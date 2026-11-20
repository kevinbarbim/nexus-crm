package br.com.nexoimob.entities;

public enum TipoAtividade {
    LIGACAO("Ligação"),
    MENSAGEM("Mensagem"),
    DOCUMENTACAO("Documentação"),
    OUTRO("Outro");

    private final String descricao;

    TipoAtividade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
