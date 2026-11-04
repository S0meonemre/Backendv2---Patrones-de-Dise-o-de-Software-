package com.financorp.serf.reportes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CatalogoPlantillasTest {
    @Test void cadaReporteRecibeUnaCopiaIndependienteDelPrototipo() {
        var catalogo = new CatalogoPlantillas();
        var primero = catalogo.clonar("MENSUAL");
        var segundo = catalogo.clonar("MENSUAL");
        primero.getConclusionesBase().add("Conclusion exclusiva del primer reporte");
        assertNotSame(primero, segundo);
        assertFalse(segundo.getConclusionesBase().contains("Conclusion exclusiva del primer reporte"));
        assertFalse(catalogo.clonar("MENSUAL").getConclusionesBase().contains("Conclusion exclusiva del primer reporte"));
    }
}
