package com.financorp.serf.reportes;

import java.util.Map;

public class MarcaAguaDecorator extends ReporteDecorator {

    public MarcaAguaDecorator(ReporteComponent envuelto) {
        super(envuelto);
    }

    @Override
    public Map<String, Object> generarContenido() {
        Map<String, Object> contenido = super.generarContenido();
        contenido.put("marcaAgua", "CONFIDENCIAL - FinanCorp S.A.");
        return contenido;
    }
}
