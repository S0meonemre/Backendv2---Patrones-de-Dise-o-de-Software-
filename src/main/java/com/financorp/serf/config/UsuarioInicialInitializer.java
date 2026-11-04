package com.financorp.serf.config;

import com.financorp.serf.model.Usuario;
import com.financorp.serf.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UsuarioInicialInitializer implements ApplicationRunner {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final AutenticacionProperties properties;

    public UsuarioInicialInitializer(UsuarioRepository usuarios, PasswordEncoder encoder,
                                    AutenticacionProperties properties) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarios.existsByUsername(properties.usuarioInicial())) return;
        String password = properties.claveInicial();
        if (password == null || password.isBlank() || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("Configura ADMIN_PASSWORD en .env: al menos 8 caracteres y maximo 72 bytes UTF-8.");
        }
        usuarios.save(new Usuario(properties.usuarioInicial(), encoder.encode(password), "ADMIN"));
    }
}
