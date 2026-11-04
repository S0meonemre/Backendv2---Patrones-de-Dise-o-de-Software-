package com.financorp.serf.dto;

import com.financorp.serf.model.Usuario;

public record UsuarioResponse(Long id, String username, String rol) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getRol());
    }
}
