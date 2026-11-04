package com.financorp.serf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Value("${serf.cors.origins}")
    private String origenes;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(origenes.split(","))
                .allowedHeaders("Authorization", "Content-Type")
                .allowedMethods("GET", "POST", "OPTIONS");
    }
}
