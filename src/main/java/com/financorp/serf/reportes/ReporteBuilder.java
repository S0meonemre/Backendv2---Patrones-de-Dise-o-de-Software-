package com.financorp.serf.reportes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Patron Builder: ensambla el reporte paso a paso (plantilla, periodo, secciones). */
public class ReporteBuilder {

    private PlantillaReporte plantilla;
    private String periodo;
    private String monedaCorporativa;
    private List<SeccionReporte> secciones = new ArrayList<>();
    private final Map<String, Object> datos = new java.util.LinkedHashMap<>();

    public ReporteBuilder conResumen(Map<String, Object> resumen) { datos.put("resumen", resumen); return this; }
    public ReporteBuilder conTablaVentas(List<Map<String, Object>> ventas) { datos.put("tablaVentas", ventas); return this; }
    public ReporteBuilder conGraficos(List<Map<String, Object>> graficos) { datos.put("graficos", graficos); return this; }
    public ReporteBuilder conAlcance(Map<String, Object> alcance) { datos.put("alcance", alcance); return this; }

    public ReporteBuilder conPlantilla(PlantillaReporte plantilla) {
        this.plantilla = plantilla;
        return this;
    }

    public ReporteBuilder conPeriodo(String periodo) {
        this.periodo = periodo;
        return this;
    }

    public ReporteBuilder conMonedaCorporativa(String monedaCorporativa) {
        this.monedaCorporativa = monedaCorporativa;
        return this;
    }

    public ReporteBuilder conSecciones(List<SeccionReporte> secciones) {
        this.secciones = secciones;
        return this;
    }

    public ReporteGenerado build() {
        if (plantilla == null) {
            throw new IllegalStateException("No se puede construir el reporte sin una plantilla");
        }
        return new ReporteGenerado(
                plantilla.getTipo(),
                periodo,
                monedaCorporativa,
                plantilla.getTitulo(),
                secciones,
                plantilla.getConclusionesBase(), datos);
    }
}
