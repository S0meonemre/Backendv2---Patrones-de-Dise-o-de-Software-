package com.financorp.serf.config;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Carga las tasas de cambio de application.properties en el Singleton
 * ConfiguracionGlobal una sola vez al arrancar la aplicacion.
 */
@Component
public class ConfiguracionGlobalInitializer implements ApplicationRunner {

    @Value("${serf.config.tasas.CNY}")
    private BigDecimal tasaCny;

    @Value("${serf.config.tasas.PEN}")
    private BigDecimal tasaPen;

    @Value("${serf.config.tasas.USD}")
    private BigDecimal tasaUsd;

    @Value("${serf.config.tasas.EUR}")
    private BigDecimal tasaEur;

    @Override
    public void run(ApplicationArguments args) {
        ConfiguracionGlobal config = ConfiguracionGlobal.getInstance();
        config.registrarTasa("CNY", tasaCny);
        config.registrarTasa("PEN", tasaPen);
        config.registrarTasa("USD", tasaUsd);
        config.registrarTasa("EUR", tasaEur);
    }
}
