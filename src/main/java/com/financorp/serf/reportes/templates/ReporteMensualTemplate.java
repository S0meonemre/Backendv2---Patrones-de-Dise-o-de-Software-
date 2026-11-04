package com.financorp.serf.reportes.templates;

import com.financorp.serf.reportes.PlantillaReporte;
import java.util.List;

public class ReporteMensualTemplate extends PlantillaReporte {
    public ReporteMensualTemplate() {
        super("MENSUAL", "Reporte Mensual de Ingresos",
                List.of("Revisar variaciones respecto al mes anterior."));
    }
}
