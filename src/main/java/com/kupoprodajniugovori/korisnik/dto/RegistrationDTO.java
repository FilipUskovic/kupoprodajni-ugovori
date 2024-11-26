package com.kupoprodajniugovori.korisnik.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationDTO(
        @NotBlank(message = "Username je obavezan")
        String username,

        @NotBlank(message = "Password je obavezan")
        @Size(min = 6, message = "Password mora imati najmanje 6 znakova")
        String password
) {
}
