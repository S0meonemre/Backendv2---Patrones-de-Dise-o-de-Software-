package com.financorp.serf.reportes;

import java.util.ArrayList;
import java.util.List;

public class PlantillaReporte implements ReportePrototype {

    private final String tipo;
    private final String titulo;
    private final List<String> conclusionesBase;

    public PlantillaReporte(String tipo, String titulo, List<String> conclusionesBase) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.conclusionesBase = conclusionesBase;
    }

    @Override
    public PlantillaReporte clonar() {
        // Copia profunda de la lista para que el clon no dependa del original.
        return new PlantillaReporte(tipo, titulo, new ArrayList<>(conclusionesBase));
    }

    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public List<String> getConclusionesBase() { return conclusionesBase; }
}
