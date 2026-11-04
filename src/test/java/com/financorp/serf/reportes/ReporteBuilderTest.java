package com.financorp.serf.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.financorp.serf.reportes.templates.ReporteMensualTemplate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReporteBuilderTest {

    @Test
    void ensamblaReporteConPlantillaYSecciones() {
        PlantillaReporte plantilla = new ReporteMensualTemplate().clonar();
        List<SeccionReporte> secciones = List.of(new SeccionSimple("Peru", new java.math.BigDecimal("100")));

        ReporteGenerado reporte = new ReporteBuilder()
                .conPlantilla(plantilla)
                .conPeriodo("2026-10")
                .conMonedaCorporativa("EUR")
                .conSecciones(secciones)
                .build();

        var contenido = reporte.generarContenido();
        assertEquals("MENSUAL", contenido.get("tipo"));
        assertEquals("2026-10", contenido.get("periodo"));
        assertEquals("Reporte Mensual de Ingresos", contenido.get("titulo"));
    }

    @Test
    void fallaSinPlantilla() {
        assertThrows(IllegalStateException.class, () -> new ReporteBuilder().build());
    }
}
