package com.financorp.serf.config;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class ConfiguracionGlobalTest {

    @Test
    void getInstanceSiempreDevuelveLaMismaInstancia() {
        ConfiguracionGlobal a = ConfiguracionGlobal.getInstance();
        ConfiguracionGlobal b = ConfiguracionGlobal.getInstance();
        assertSame(a, b);
    }
}
