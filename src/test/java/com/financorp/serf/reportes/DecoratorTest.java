package com.financorp.serf.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class DecoratorTest {

    @Test
    void decoradoresAgreganCamposSinMutarElBase() {
        ReporteGenerado base = new ReporteGenerado("MENSUAL", "2026-10", "EUR", "Reporte", List.of(), List.of());

        var contenidoBase = base.generarContenido();
        assertFalse(contenidoBase.containsKey("marcaAgua"));
        assertFalse(contenidoBase.containsKey("firmaDigital"));

        ReporteComponent decorado = new FirmaDigitalDecorator(new MarcaAguaDecorator(base),
                new FirmaReporteService(new com.fasterxml.jackson.databind.ObjectMapper()));
        var contenidoDecorado = decorado.generarContenido();

        assertTrue(contenidoDecorado.containsKey("marcaAgua"));
        assertTrue(contenidoDecorado.containsKey("firmaDigital"));
        assertEquals("CONFIDENCIAL - FinanCorp S.A.", contenidoDecorado.get("marcaAgua"));

        // el base no debe haberse mutado por el decorador
        assertFalse(base.generarContenido().containsKey("marcaAgua"));
    }
}
