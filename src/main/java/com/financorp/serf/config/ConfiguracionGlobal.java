package com.financorp.serf.config;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.math.RoundingMode;

/**
 * Singleton clasico: constructor privado + unica instancia estatica.
 * Punto de acceso global a la moneda corporativa y las tasas de cambio,
 * usado por el modulo de ventas y el modulo de reportes.
 */
public final class ConfiguracionGlobal {

    private static ConfiguracionGlobal instancia;

    private final String monedaCorporativa;
    private final Map<String, BigDecimal> tasasDeCambio = new HashMap<>();

    private ConfiguracionGlobal(String monedaCorporativa) {
        this.monedaCorporativa = monedaCorporativa;
    }

    public static synchronized ConfiguracionGlobal getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracionGlobal("EUR");
        }
        return instancia;
    }

    /** Usado una sola vez al arrancar la app (ver ConfiguracionGlobalInitializer). */
    void registrarTasa(String moneda, BigDecimal tasaAMonedaCorporativa) {
        tasasDeCambio.put(moneda, tasaAMonedaCorporativa);
    }

    public String getMonedaCorporativa() {
        return monedaCorporativa;
    }

    public Map<String, BigDecimal> getTasasDeCambio() {
        return Map.copyOf(tasasDeCambio);
    }

    public BigDecimal convertirAMonedaCorporativa(BigDecimal monto, String monedaOrigen) {
        BigDecimal tasa = tasasDeCambio.get(monedaOrigen);
        if (tasa == null) {
            throw new IllegalArgumentException("Moneda no soportada: " + monedaOrigen);
        }
        return monto.multiply(tasa).setScale(2, RoundingMode.HALF_UP);
    }
}
