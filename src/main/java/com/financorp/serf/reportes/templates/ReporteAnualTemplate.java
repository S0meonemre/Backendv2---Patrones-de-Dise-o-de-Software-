package com.financorp.serf.reportes.templates;

import com.financorp.serf.reportes.PlantillaReporte;
import java.util.List;

public class ReporteAnualTemplate extends PlantillaReporte {
    public ReporteAnualTemplate() {
        super("ANUAL", "Reporte Anual Consolidado",
                List.of("Consolidar resultados de todas las filiales para el cierre anual."));
    }
}
