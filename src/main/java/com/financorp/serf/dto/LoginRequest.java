package com.financorp.serf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Pattern(regexp = "[a-zA-Z0-9._-]{3,80}") String username,
        @NotBlank @Size(max = 72) @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String password) {
    @Override
    public String toString() { return "LoginRequest[credenciales ocultas]"; }
}
