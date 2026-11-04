package com.financorp.serf.reportes;

import java.math.BigDecimal;
import java.util.Map;

/** Patron Composite: componente comun a secciones simples y compuestas. */
public interface SeccionReporte {
    String getNombre();
    BigDecimal getTotal();
    Map<String, Object> toMap();
}
