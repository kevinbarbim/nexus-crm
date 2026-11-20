package br.com.nexoimob.services;

import br.com.nexoimob.entities.*;
import br.com.nexoimob.web.BlocoRelatorio;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RelatorioService {
    private final JdbcTemplate jdbc;
    private final AcessoService acesso;
    private final LeadService leads;
    private final AgendaService agenda;
    private final QualidadeService qualidade;

    public RelatorioService(JdbcTemplate jdbc, AcessoService acesso, LeadService leads,
            AgendaService agenda, QualidadeService qualidade) {
        this.jdbc = jdbc;
        this.acesso = acesso;
        this.leads = leads;
        this.agenda = agenda;
        this.qualidade = qualidade;
    }

    public List<BlocoRelatorio> consultarSQL() {
        List<BlocoRelatorio> blocos = new ArrayList<>();
        blocos.add(new BlocoRelatorio("Funil por corretor",
                porCorretor("SELECT * FROM dbo.vw_funil_corretor")));
        blocos.add(new BlocoRelatorio("Visitas e deslocamentos",
                porCorretor("SELECT * FROM dbo.vw_visitas_ambiental")));
        if (acesso.ehAdministrador()) {
            blocos.add(new BlocoRelatorio("Pareto de perdas",
                    jdbc.queryForList("SELECT * FROM dbo.vw_pareto_perdas "
                            + "ORDER BY quantidade DESC, motivo_perda")));
        }
        blocos.add(new BlocoRelatorio("Tempo até o primeiro contato",
                porCorretor("SELECT id, responsavel_id, "
                        + "dbo.fn_tempo_primeiro_contato(id) AS horas_contato FROM dbo.leads")));
        Long responsavel = null;
        if (!acesso.ehAdministrador()) {
            responsavel = acesso.usuarioAtual().getId();
        }
        blocos.add(new BlocoRelatorio("Leads dos últimos 12 meses",
                jdbc.queryForList("SELECT * FROM dbo.fn_leads_periodo(?,?,?)",
                        LocalDateTime.now().minusYears(1), LocalDateTime.now().plusDays(1),
                        responsavel)));
        blocos.add(new BlocoRelatorio("Resumo por corretor",
                jdbc.queryForList("SELECT * FROM dbo.fn_resumo_corretor(?)", responsavel)));
        return blocos;
    }

    private List<Map<String, Object>> porCorretor(String consulta) {
        // O SQL é fixo; somente o ID vem como parâmetro.
        if (acesso.ehAdministrador()) {
            return jdbc.queryForList(consulta);
        }
        return jdbc.queryForList(consulta + " WHERE responsavel_id = ?",
                acesso.usuarioAtual().getId());
    }

    public String exportarCSV() {
        Map<Long, List<Visita>> visitasPorLead = new HashMap<>();
        for (Visita visita : agenda.listarVisitas()) {
            if (visita.getSituacao() == SituacaoVisita.REALIZADA) {
                Long id = visita.getLead().getId();
                if (!visitasPorLead.containsKey(id)) {
                    visitasPorLead.put(id, new ArrayList<>());
                }
                visitasPorLead.get(id).add(visita);
            }
        }
        Map<Long, Integer> ocorrenciasPorLead = new HashMap<>();
        for (Ocorrencia ocorrencia : qualidade.listar()) {
            Long id = ocorrencia.getLead().getId();
            ocorrenciasPorLead.put(id, ocorrenciasPorLead.getOrDefault(id, 0) + 1);
        }
        StringBuilder csv = new StringBuilder("\uFEFFlead_id;responsavel_id;origem;etapa;"
                + "criado_em;primeiro_contato_em;encerrado_em;horas_contato;dias_encerramento;"
                + "visitas_realizadas;visitas_virtuais;km_realizados;motivo_perda;ocorrencias\n");
        for (Lead lead : leads.listar()) {
            List<Visita> realizadas = visitasPorLead.getOrDefault(lead.getId(), List.of());
            int virtuais = 0;
            BigDecimal km = BigDecimal.ZERO;
            for (Visita visita : realizadas) {
                if (visita.isVirtual()) {
                    virtuais++;
                }
                km = km.add(visita.getKm());
            }
            Object[] valores = {
                lead.getId(), lead.getResponsavel().getId(), lead.getOrigem().name(),
                lead.getEtapa().name(), lead.getCriadoEm(), lead.getPrimeiroContatoEm(),
                lead.getEncerradoEm(), tempoDecorrido(lead.getCriadoEm(),
                        lead.getPrimeiroContatoEm(), 3600),
                tempoDecorrido(lead.getCriadoEm(), lead.getEncerradoEm(), 86400),
                realizadas.size(), virtuais, km, lead.getMotivoPerda(),
                ocorrenciasPorLead.getOrDefault(lead.getId(), 0)
            };
            for (int i = 0; i < valores.length; i++) {
                if (i > 0) {
                    csv.append(';');
                }
                csv.append(celulaCSV(valores[i]));
            }
            csv.append('\n');
        }
        return csv.toString();
    }

    private Double tempoDecorrido(LocalDateTime inicio, LocalDateTime fim, int divisor) {
        if (fim == null) {
            return null;
        }
        return Duration.between(inicio, fim).toSeconds() / (double) divisor;
    }

    private String celulaCSV(Object valor) {
        String texto = valor == null ? "" : valor.toString();
        // Evita fórmulas ao abrir o CSV em uma planilha.
        if (texto.startsWith("=") || texto.startsWith("+")
                || texto.startsWith("-") || texto.startsWith("@")) {
            texto = "'" + texto;
        }
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }
}
