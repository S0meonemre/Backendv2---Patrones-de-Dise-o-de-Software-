package com.financorp.serf.reportes;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PeriodoReporteTest {
    @Test void incluyeFinDeMesBisiesto() {
        var rango = PeriodoReporte.de("MENSUAL", "2024-02");
        assertTrue(rango.contiene(LocalDate.of(2024, 2, 29)));
        assertFalse(rango.contiene(LocalDate.of(2024, 3, 1)));
    }
    @Test void trimestreTieneLimitesExactos() {
        var rango = PeriodoReporte.de("TRIMESTRAL", "2026-Q4");
        assertEquals(LocalDate.of(2026, 10, 1), rango.inicio());
        assertEquals(LocalDate.of(2026, 12, 31), rango.fin());
        assertThrows(IllegalArgumentException.class, () -> PeriodoReporte.de("TRIMESTRAL", "2026-Q5"));
    }
    @Test void clonarNoModificaElPrototipo() {
        var original = new com.financorp.serf.reportes.templates.ReporteMensualTemplate();
        var copia = original.clonar();
        copia.getConclusionesBase().add("Conclusión propia de una filial");
        assertEquals(1, original.getConclusionesBase().size());
        assertEquals(2, copia.getConclusionesBase().size());
    }
}
