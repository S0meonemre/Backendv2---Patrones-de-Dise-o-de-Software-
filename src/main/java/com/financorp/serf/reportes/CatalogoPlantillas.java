package com.financorp.serf.reportes;

import com.financorp.serf.reportes.templates.*;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Conserva prototipos base; cada solicitud recibe un clon independiente. */
@Component
public class CatalogoPlantillas {
    private final Map<String, ReportePrototype> plantillas = Map.of(
            "MENSUAL", new ReporteMensualTemplate(),
            "TRIMESTRAL", new ReporteTrimestralTemplate(),
            "ANUAL", new ReporteAnualTemplate());
    public PlantillaReporte clonar(String tipo) {
        ReportePrototype base = plantillas.get(tipo);
        if (base == null) throw new IllegalArgumentException("Tipo de reporte inválido.");
        return base.clonar();
    }
}
