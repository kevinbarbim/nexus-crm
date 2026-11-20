package br.com.nexoimob.services;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.forms.ImovelForm;
import br.com.nexoimob.repositories.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ImovelService {
    private final ImovelRepository imoveis;
    private final InteresseRepository interesses;
    private final AcessoService acesso;

    public ImovelService(ImovelRepository imoveis, InteresseRepository interesses,
            AcessoService acesso) {
        this.imoveis = imoveis;
        this.interesses = interesses;
        this.acesso = acesso;
    }

    public List<Imovel> listar() {
        acesso.usuarioAtual();
        return imoveis.findAllByOrderByIdDesc();
    }

    public Imovel buscar(Long id) {
        acesso.usuarioAtual();
        return imoveis.findById(id).orElseThrow();
    }

    public boolean disponivel(Imovel imovel) {
        return imovel.getSituacao() == SituacaoImovel.DISPONIVEL
                || imovel.getSituacao() == SituacaoImovel.RESERVADO;
    }

    public List<Imovel> listarDisponiveis() {
        List<Imovel> resultado = new ArrayList<>();
        for (Imovel imovel : listar()) {
            if (disponivel(imovel)) {
                resultado.add(imovel);
            }
        }
        return resultado;
    }

    public List<Imovel> listarInteresses(Lead lead) {
        acesso.verificarLead(lead);
        List<Imovel> resultado = new ArrayList<>();
        for (Interesse interesse : interesses.findByLeadId(lead.getId())) {
            if (disponivel(interesse.getImovel())) {
                resultado.add(interesse.getImovel());
            }
        }
        return resultado;
    }

    public Imovel salvar(Long id, ImovelForm form) {
        Imovel imovel;
        if (id == null) {
            imovel = new Imovel();
        } else {
            imovel = buscar(id);
        }
        if (id != null) {
            acesso.verificarImovel(imovel);
        }
        if (form.getSituacao() == SituacaoImovel.NEGOCIADO
                && imovel.getSituacao() != SituacaoImovel.NEGOCIADO) {
            throw new IllegalArgumentException("Imóvel negociado depende de proposta aceita");
        }
        if (imovel.getSituacao() == SituacaoImovel.NEGOCIADO
                && form.getSituacao() != SituacaoImovel.NEGOCIADO) {
            throw new IllegalArgumentException("Um imóvel negociado não pode ser reaberto");
        }
        imovel.setTitulo(form.getTitulo());
        imovel.setTipo(form.getTipo());
        imovel.setFinalidade(form.getFinalidade());
        imovel.setCidade(form.getCidade());
        imovel.setBairro(form.getBairro());
        imovel.setQuartos(form.getQuartos());
        imovel.setArea(form.getArea());
        imovel.setValor(form.getValor());
        imovel.setSituacao(form.getSituacao());
        imovel.setDescricao(form.getDescricao());
        if (id == null) {
            imovel.setCriadoPor(acesso.usuarioAtual());
        }
        return imoveis.saveAndFlush(imovel);
    }

    public void excluir(Long id) {
        Imovel imovel = buscar(id);
        acesso.verificarImovel(imovel);
        imoveis.delete(imovel);
        imoveis.flush(); // A FK impede excluir um imóvel que já está vinculado.
    }
}
