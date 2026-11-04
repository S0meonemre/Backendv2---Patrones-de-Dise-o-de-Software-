package com.financorp.serf.reportes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Componente concreto: el reporte ya ensamblado por el Builder, sin decorar. */
public class ReporteGenerado implements ReporteComponent {

    private final String tipo;
    private final String periodo;
    private final String monedaCorporativa;
    private final String titulo;
    private final List<SeccionReporte> secciones;
    private final List<String> conclusiones;
    private final Map<String, Object> datos;

    public ReporteGenerado(String tipo, String periodo, String monedaCorporativa, String titulo,
                            List<SeccionReporte> secciones, List<String> conclusiones) {
        this(tipo, periodo, monedaCorporativa, titulo, secciones, conclusiones, Map.of());
    }

    public ReporteGenerado(String tipo, String periodo, String monedaCorporativa, String titulo,
                            List<SeccionReporte> secciones, List<String> conclusiones, Map<String, Object> datos) {
        this.tipo = tipo;
        this.periodo = periodo;
        this.monedaCorporativa = monedaCorporativa;
        this.titulo = titulo;
        this.secciones = List.copyOf(secciones);
        this.conclusiones = List.copyOf(conclusiones);
        this.datos = Map.copyOf(datos);
    }

    @Override
    public Map<String, Object> generarContenido() {
        Map<String, Object> contenido = new LinkedHashMap<>();
        contenido.put("tipo", tipo);
        contenido.put("periodo", periodo);
        contenido.put("monedaCorporativa", monedaCorporativa);
        contenido.put("titulo", titulo);
        contenido.put("secciones", secciones.stream().map(SeccionReporte::toMap).toList());
        contenido.put("conclusiones", conclusiones);
        contenido.putAll(datos);
        return contenido;
    }
}
