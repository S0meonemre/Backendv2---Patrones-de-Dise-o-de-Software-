package com.financorp.serf.reportes;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Nodo del Composite: agrupa subsecciones con anidamiento ilimitado. */
public class SeccionCompuesta implements SeccionReporte {

    private final String nombre;
    private final List<SeccionReporte> subsecciones;

    public SeccionCompuesta(String nombre, List<SeccionReporte> subsecciones) {
        this.nombre = nombre;
        this.subsecciones = subsecciones;
    }

    @Override
    public String getNombre() { return nombre; }

    @Override
    public BigDecimal getTotal() {
        return subsecciones.stream()
                .map(SeccionReporte::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<SeccionReporte> getSubsecciones() { return subsecciones; }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("nombre", nombre);
        map.put("total", getTotal());
        map.put("subsecciones", subsecciones.stream().map(SeccionReporte::toMap).toList());
        return map;
    }
}
