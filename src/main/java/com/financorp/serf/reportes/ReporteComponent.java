package com.financorp.serf.reportes;

import java.util.Map;

/** Patron Decorator: interfaz comun entre el reporte base y sus decoradores. */
public interface ReporteComponent {
    Map<String, Object> generarContenido();
}
