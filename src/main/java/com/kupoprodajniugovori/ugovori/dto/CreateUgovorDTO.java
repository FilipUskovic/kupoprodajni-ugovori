package com.kupoprodajniugovori.ugovori.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateUgovorDTO(
        @NotBlank(message = "polje kupac ne moze biti prazno")
        String kupac,
        @NotNull(message = "Morate unijeti valjani datum akontacije")
        LocalDate datumAkontacije,
        @NotNull(message = "morate unijeti valjani rok isporuke")
        LocalDate rokIsporuke
) {
}
