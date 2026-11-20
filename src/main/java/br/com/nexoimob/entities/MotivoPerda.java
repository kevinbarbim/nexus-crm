package br.com.nexoimob.entities;

public enum MotivoPerda {
    ORCAMENTO("Orçamento"),
    SEM_RETORNO("Sem retorno"),
    IMOVEL_INADEQUADO("Imóvel inadequado"),
    DOCUMENTACAO("Documentação"),
    OUTRO("Outro");

    private final String descricao;

    MotivoPerda(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
