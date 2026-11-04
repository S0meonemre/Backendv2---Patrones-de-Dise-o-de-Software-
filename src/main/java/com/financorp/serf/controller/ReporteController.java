package com.financorp.serf.controller;

import com.financorp.serf.dto.ReporteRequest;
import com.financorp.serf.dto.ReporteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Content;
import com.financorp.serf.reportes.ReporteFacade;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reportes corporativos por período y filial")
public class ReporteController {

    private final ReporteFacade reporteFacade;
    private final ObjectMapper mapper;

    public ReporteController(ReporteFacade reporteFacade, ObjectMapper mapper) {
        this.reporteFacade = reporteFacade;
        this.mapper = mapper;
    }

    @PostMapping("/generar")
    @Operation(summary = "Generar reporte completo",
            description = "Períodos: MENSUAL AAAA-MM; TRIMESTRAL AAAA-Q1 a AAAA-Q4; ANUAL AAAA. filialId nulo consolida todas las filiales.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "Mensual", value = "{\"tipo\":\"MENSUAL\",\"periodo\":\"2026-11\",\"marcaAgua\":true,\"firmaDigital\":true}"),
            @ExampleObject(name = "Trimestral", value = "{\"tipo\":\"TRIMESTRAL\",\"periodo\":\"2026-Q4\",\"filialId\":1}"),
            @ExampleObject(name = "Anual", value = "{\"tipo\":\"ANUAL\",\"periodo\":\"2026\"}") }))
    public ReporteResponse generar(@Valid @RequestBody ReporteRequest request) {
        return mapper.convertValue(reporteFacade.generarReporte(request), ReporteResponse.class);
    }
}
