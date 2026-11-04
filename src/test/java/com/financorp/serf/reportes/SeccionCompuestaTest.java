package com.financorp.serf.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class SeccionCompuestaTest {

    @Test
    void totalEsLaSumaDeLasSubsecciones() {
        SeccionReporte peru = new SeccionSimple("Peru", new BigDecimal("100"));
        SeccionReporte espana = new SeccionSimple("Espana", new BigDecimal("50"));
        SeccionCompuesta ingresosPorPais = new SeccionCompuesta("Ingresos por pais", List.of(peru, espana));

        assertEquals(new BigDecimal("150"), ingresosPorPais.getTotal());
    }

    @Test
    void permiteAnidamientoDeSeccionesCompuestas() {
        SeccionReporte hojaA = new SeccionSimple("A", new BigDecimal("10"));
        SeccionCompuesta nivel2 = new SeccionCompuesta("Nivel2", List.of(hojaA));
        SeccionCompuesta nivel1 = new SeccionCompuesta("Nivel1", List.of(nivel2));

        assertEquals(new BigDecimal("10"), nivel1.getTotal());
    }
}
