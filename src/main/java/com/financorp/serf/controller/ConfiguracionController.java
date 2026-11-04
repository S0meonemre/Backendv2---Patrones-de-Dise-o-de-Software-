package com.financorp.serf.controller;

import com.financorp.serf.config.ConfiguracionGlobal;
import com.financorp.serf.dto.ConfiguracionResponse;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configuracion")
@Tag(name = "Configuración")
public class ConfiguracionController {

    @GetMapping
    @Operation(summary = "Consultar moneda corporativa y tasas educativas hacia EUR")
    public ConfiguracionResponse obtener() {
        ConfiguracionGlobal config = ConfiguracionGlobal.getInstance();
        return new ConfiguracionResponse(config.getMonedaCorporativa(), config.getTasasDeCambio());
    }
}
