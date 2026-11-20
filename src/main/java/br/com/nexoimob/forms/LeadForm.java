package br.com.nexoimob.forms;

import br.com.nexoimob.entities.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class LeadForm {
    @NotBlank @Size(max = 120)
    private String nome;
    @Email @Size(max = 180)
    private String email;
    @NotBlank @Size(max = 30)
    @Pattern(regexp = "([0-9]{10,11}|55[0-9]{10,11})",
            message = "Informe DDD e telefone com 10 ou 11 dígitos. O código 55 é opcional.")
    private String telefone;
    @NotNull
    private Origem origem;
    @NotNull @DecimalMin("0")
    @Digits(integer = 17, fraction = 2, message = "Use até 17 dígitos inteiros e 2 casas decimais.")
    private BigDecimal orcamento;
    @Size(max = 2000)
    private String observacoes;

    private Long responsavelId;
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null) {
            this.telefone = null;
            return;
        }
        // Remove a formatação, mas mantém letras para a validação.
        String numero = telefone.replaceAll("[\\s().-]", "");
        if (numero.startsWith("+55")) {
            numero = numero.substring(1);
        }
        this.telefone = numero;
    }

    public Origem getOrigem() {
        return origem;
    }

    public void setOrigem(Origem origem) {
        this.origem = origem;
    }

    public BigDecimal getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(BigDecimal orcamento) {
        this.orcamento = orcamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Long getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Long responsavelId) {
        this.responsavelId = responsavelId;
    }

}
