package com.financorp.serf.dto;

public record LoginResponse(String token, String tokenType, long expiresIn, UsuarioResponse usuario) {}
