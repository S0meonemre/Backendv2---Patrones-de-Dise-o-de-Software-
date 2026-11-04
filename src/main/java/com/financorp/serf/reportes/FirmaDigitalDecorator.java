package com.financorp.serf.reportes;

import java.util.Map;

public class FirmaDigitalDecorator extends ReporteDecorator {
    private final FirmaReporteService firma;
    public FirmaDigitalDecorator(ReporteComponent envuelto, FirmaReporteService firma) {
        super(envuelto);
        this.firma = firma;
    }
    @Override
    public Map<String, Object> generarContenido() {
        Map<String, Object> contenido = super.generarContenido();
        contenido.put("firmaDigital", firma.firmar(contenido));
        return contenido;
    }
}
