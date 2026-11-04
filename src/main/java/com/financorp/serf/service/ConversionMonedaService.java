package com.financorp.serf.service;

import com.financorp.serf.config.ConfiguracionGlobal;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

/**
 * Convierte montos a la moneda corporativa usando el Singleton ConfiguracionGlobal.
 */
@Service
public class ConversionMonedaService {

    public BigDecimal aMonedaCorporativa(BigDecimal monto, String monedaOrigen) {
        return ConfiguracionGlobal.getInstance().convertirAMonedaCorporativa(monto, monedaOrigen);
    }

    public String monedaCorporativa() {
        return ConfiguracionGlobal.getInstance().getMonedaCorporativa();
    }
}
