package com.financorp.serf.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ConfiguracionResponse(String monedaCorporativa, Map<String, BigDecimal> tasasDeCambio) { }
