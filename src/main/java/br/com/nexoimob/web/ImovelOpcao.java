package br.com.nexoimob.web;

/** Resposta do seletor, sem dados de clientes. */
public class ImovelOpcao {
    private final Long id;
    private final String titulo;

    public ImovelOpcao(Long id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }
}
