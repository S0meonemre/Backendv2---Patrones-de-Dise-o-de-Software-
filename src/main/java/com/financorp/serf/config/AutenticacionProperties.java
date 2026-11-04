package com.financorp.serf.config;

import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "serf.auth")
public record AutenticacionProperties(String jwtSecret, long ttlSegundos,
                                     String usuarioInicial, String claveInicial) {
    public AutenticacionProperties {
        if (jwtSecret == null || jwtSecret.isBlank()
                || jwtSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("Configura JWT_SECRET en .env con al menos 32 bytes.");
        }
        if (ttlSegundos < 60 || ttlSegundos > 86400) {
            throw new IllegalArgumentException("JWT_TTL_SECONDS debe estar entre 60 y 86400.");
        }
        if (usuarioInicial == null || !usuarioInicial.matches("[a-zA-Z0-9._-]{3,80}")) {
            throw new IllegalArgumentException("ADMIN_USERNAME debe tener de 3 a 80 letras, numeros, puntos, guiones o guiones bajos.");
        }
    }
    @Override
    public String toString() { return "AutenticacionProperties[credenciales ocultas]"; }
}
