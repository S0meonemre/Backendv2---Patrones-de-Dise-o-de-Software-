package com.financorp.serf.service;

import com.financorp.serf.config.AutenticacionProperties;
import com.financorp.serf.config.SecurityConfig;
import com.financorp.serf.dto.LoginRequest;
import com.financorp.serf.dto.LoginResponse;
import com.financorp.serf.dto.UsuarioResponse;
import com.financorp.serf.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacionService {
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarios;
    private final JwtEncoder encoder;
    private final AutenticacionProperties properties;

    public AutenticacionService(AuthenticationManager authenticationManager, UsuarioRepository usuarios,
                                JwtEncoder encoder, AutenticacionProperties properties) {
        this.authenticationManager = authenticationManager;
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadCredentialsException("Credenciales invalidas.");
        }
        var authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
        var usuario = usuarios.findByUsername(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas."));
        Instant now = Instant.now();
        var claims = JwtClaimsSet.builder().issuer(SecurityConfig.JWT_ISSUER)
                .subject(usuario.getUsername()).issuedAt(now).expiresAt(now.plusSeconds(properties.ttlSegundos()))
                .claim("rol", usuario.getRol()).build();
        String token = encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new LoginResponse(token, "Bearer", properties.ttlSegundos(), UsuarioResponse.de(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse usuarioActual(String username) {
        return usuarios.findByUsername(username).map(UsuarioResponse::de)
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas."));
    }
}
