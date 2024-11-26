package com.kupoprodajniugovori.artikli.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateArtiklDTO(
        @NotBlank
        String naziv,
        @NotBlank
        String dobavljac,
        @Positive
        Integer kolicina
) {
}
