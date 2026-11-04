package com.financorp.serf.reportes;

import java.util.LinkedHashMap;
import java.util.Map;

/** Patron Decorator: base para anadir caracteristicas sin tocar ReporteGenerado. */
public abstract class ReporteDecorator implements ReporteComponent {

    protected final ReporteComponent envuelto;

    protected ReporteDecorator(ReporteComponent envuelto) {
        this.envuelto = envuelto;
    }

    @Override
    public Map<String, Object> generarContenido() {
        return new LinkedHashMap<>(envuelto.generarContenido());
    }
}
