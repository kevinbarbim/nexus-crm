package br.com.nexoimob.web;

import java.util.List;
import java.util.Map;

public class BlocoRelatorio {
    private final String titulo;
    private final List<Map<String, Object>> linhas;

    public BlocoRelatorio(String titulo, List<Map<String, Object>> linhas) {
        this.titulo = titulo;
        this.linhas = linhas;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<Map<String, Object>> getLinhas() {
        return linhas;
    }
}
