package com.financorp.serf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financorp.serf.config.SecurityConfig;
import com.financorp.serf.repository.UsuarioRepository;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"demo", "test"})
class AutenticacionIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UsuarioRepository usuarios;
    @Autowired PasswordEncoder passwords;
    @Autowired JwtEncoder jwtEncoder;

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder login(String username, String password) throws Exception {
        return post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("username", username, "password", password)));
    }

    private String token() throws Exception {
        var result = mvc.perform(login("admin-test", "ClavePruebas-2026"))
                .andExpect(status().isOk()).andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test void loginUsaUsuarioPersistidoYNoExponeContrasena() throws Exception {
        var usuario = usuarios.findByUsername("admin-test").orElseThrow();
        assertNotEquals("ClavePruebas-2026", usuario.getPasswordHash());
        assertTrue(passwords.matches("ClavePruebas-2026", usuario.getPasswordHash()));
        mvc.perform(login("admin-test", "ClavePruebas-2026"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.usuario.id").value(usuario.getId()))
                .andExpect(jsonPath("$.usuario.username").value("admin-test"))
                .andExpect(jsonPath("$.usuario.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.usuario.password").doesNotExist());
    }

    @Test void rechazaClaveIncorrectaYUsuarioInexistenteConElMismoMensaje() throws Exception {
        String incorrecta = mvc.perform(login("admin-test", "ClaveIncorrecta"))
                .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
        String inexistente = mvc.perform(login("no-existe", "ClaveIncorrecta"))
                .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
        assertEquals(incorrecta, inexistente);
    }

    @Test void rechazaApiSinToken() throws Exception {
        mvc.perform(get("/api/productos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/reportes/generar").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"ANUAL\",\"periodo\":\"2026\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test void tokenPermiteConsultarUsuarioYDatos() throws Exception {
        String authorization = "Bearer " + token();
        mvc.perform(get("/api/auth/me").header("Authorization", authorization))
                .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("admin-test"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
        mvc.perform(get("/api/filiales").header("Authorization", authorization)).andExpect(status().isOk());
    }

    @Test void rechazaTokenManipulado() throws Exception {
        String token = token();
        int signature = token.lastIndexOf('.') + 1;
        String manipulado = token.substring(0, signature) + (token.charAt(signature) == 'A' ? 'B' : 'A') + token.substring(signature + 1);
        mvc.perform(get("/api/filiales").header("Authorization", "Bearer " + manipulado))
                .andExpect(status().isUnauthorized());
    }

    @Test void rechazaTokenExpiradoYAunqueEsteFirmadoSiElEmisorEsDistinto() throws Exception {
        for (String issuer : new String[]{SecurityConfig.JWT_ISSUER, "otro-sistema"}) {
            Instant now = Instant.now();
            var claims = JwtClaimsSet.builder().issuer(issuer).subject("admin-test")
                    .issuedAt(now.minusSeconds(7200))
                    .expiresAt(issuer.equals(SecurityConfig.JWT_ISSUER) ? now.minusSeconds(3600) : now.plusSeconds(3600)).build();
            String token = jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
            mvc.perform(get("/api/filiales").header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test void validaCamposDeLogin() throws Exception {
        mvc.perform(login("", "")).andExpect(status().isBadRequest());
        mvc.perform(login("admin-test", "x".repeat(73))).andExpect(status().isBadRequest());
        mvc.perform(login("admin-test", "\u00e1".repeat(40))).andExpect(status().isUnauthorized());
    }

    @Test void permitePreflightDelFrontendSinToken() throws Exception {
        mvc.perform(options("/api/filiales").header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test void swaggerPublicoDocumentaLoginYBearer() throws Exception {
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/auth/login'].post").exists())
                .andExpect(jsonPath("$.paths['/api/auth/me'].get").exists())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"));
    }
}
