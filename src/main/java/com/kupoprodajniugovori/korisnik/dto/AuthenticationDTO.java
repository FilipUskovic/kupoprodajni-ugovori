package com.kupoprodajniugovori.korisnik.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
        @NotBlank(message = "Username je obavezan")
        String username,

        @NotBlank(message = "Password je obavezan")
        String password
) {
}
