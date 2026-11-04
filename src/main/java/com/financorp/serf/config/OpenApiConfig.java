package com.financorp.serf.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI serfOpenApi() {
        return new OpenAPI().info(new Info().title("SERF · API FinanCorp").version("1.0.0")
                .description("Inventario por filial, ventas, configuración y reportes en EUR. "
                        + "Login: POST /api/auth/login. Usa Authorization: Bearer TOKEN. YAML: /v3/api-docs.yaml."))
                .addServersItem(new Server().url("/"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
