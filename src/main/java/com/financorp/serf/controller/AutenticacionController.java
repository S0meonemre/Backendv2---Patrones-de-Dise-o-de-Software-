package com.financorp.serf.controller;

import com.financorp.serf.dto.LoginRequest;
import com.financorp.serf.dto.LoginResponse;
import com.financorp.serf.dto.UsuarioResponse;
import com.financorp.serf.service.AutenticacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Login con usuario y contrasena almacenados en la base de datos")
public class AutenticacionController {
    private final AutenticacionService autenticacion;
    public AutenticacionController(AutenticacionService autenticacion) {
        this.autenticacion = autenticacion;
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Iniciar sesion", description = "Devuelve un token que se envia como Authorization: Bearer TOKEN.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(autenticacion.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Consultar el usuario autenticado")
    public ResponseEntity<UsuarioResponse> usuarioActual(Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(autenticacion.usuarioActual(principal.getName()));
    }
}
