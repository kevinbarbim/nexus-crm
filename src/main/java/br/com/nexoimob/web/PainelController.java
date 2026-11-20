package br.com.nexoimob.web;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.services.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PainelController {
    private final LeadService leads;
    private final AgendaService agenda;

    public PainelController(LeadService leads, AgendaService agenda) {
        this.leads = leads;
        this.agenda = agenda;
    }

    @GetMapping("/")
    public String painel(Model modelo) {
        List<Lead> registros = leads.listar();
        int ganhos = 0;
        int encerrados = 0;
        for (Lead lead : registros) {
            if (leads.encerrado(lead)) {
                encerrados++;
            }
            if (lead.getEtapa() == Etapa.GANHO) {
                ganhos++;
            }
        }
        List<Atividade> pendentes = new ArrayList<>();
        int atrasadas = 0;
        LocalDateTime agora = LocalDateTime.now();
        for (Atividade atividade : agenda.listarAtividades()) {
            if (atividade.getConcluidaEm() == null && !leads.encerrado(atividade.getLead())) {
                pendentes.add(atividade);
                if (atividade.getPrazo().isBefore(agora)) {
                    atrasadas++;
                }
            }
        }
        String conversao = "—";
        if (encerrados > 0) {
            conversao = String.format(Locale.forLanguageTag("pt-BR"),
                    "%.1f%%", 100.0 * ganhos / encerrados);
        }
        modelo.addAttribute("totalLeads", registros.size());
        modelo.addAttribute("ativos", registros.size() - encerrados);
        modelo.addAttribute("conversao", conversao);
        modelo.addAttribute("atrasadas", atrasadas);
        modelo.addAttribute("tarefas", pendentes.subList(0, Math.min(8, pendentes.size())));
        modelo.addAttribute("recentes", registros.subList(0, Math.min(6, registros.size())));
        modelo.addAttribute("agora", agora);
        return "home";
    }
}
