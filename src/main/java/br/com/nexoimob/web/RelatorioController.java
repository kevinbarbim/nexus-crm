package br.com.nexoimob.web;

import br.com.nexoimob.services.RelatorioService;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RelatorioController {
    private final RelatorioService relatorios;

    public RelatorioController(RelatorioService relatorios) {
        this.relatorios = relatorios;
    }

    @GetMapping("/relatorios")
    public String listar(Model modelo) {
        modelo.addAttribute("blocos", relatorios.consultarSQL());
        return "relatorios";
    }

    @GetMapping("/exportar.csv")
    public ResponseEntity<String> exportar() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=nexus_dados.csv")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(relatorios.exportarCSV());
    }
}
