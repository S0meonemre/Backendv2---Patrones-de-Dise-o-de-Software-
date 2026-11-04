package com.financorp.serf.reportes.templates;

import com.financorp.serf.reportes.PlantillaReporte;
import java.util.List;

public class ReporteTrimestralTemplate extends PlantillaReporte {
    public ReporteTrimestralTemplate() {
        super("TRIMESTRAL", "Reporte Trimestral de Gastos e Ingresos",
                List.of("Comparar gastos de importacion contra ingresos del trimestre."));
    }
}
