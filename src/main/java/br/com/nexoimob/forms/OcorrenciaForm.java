package br.com.nexoimob.forms;

import br.com.nexoimob.entities.*;
import jakarta.validation.constraints.*;

public class OcorrenciaForm {
    @NotNull
    private Long leadId;
    @NotNull
    private Categoria categoria;
    @NotBlank @Size(max = 2000)
    private String descricao;
    @Size(max = 2000)
    private String acao;
    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

}
