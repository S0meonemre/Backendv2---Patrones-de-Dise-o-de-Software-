package com.financorp.serf.reportes;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/** Hoja del Composite: una linea sin subsecciones (ej. un pais, una categoria). */
public class SeccionSimple implements SeccionReporte {

    private final String nombre;
    private final BigDecimal total;

    public SeccionSimple(String nombre, BigDecimal total) {
        this.nombre = nombre;
        this.total = total;
    }

    @Override
    public String getNombre() { return nombre; }

    @Override
    public BigDecimal getTotal() { return total; }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("nombre", nombre);
        map.put("total", total);
        return map;
    }
}
