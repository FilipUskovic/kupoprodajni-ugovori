package com.kupoprodajniugovori.ugovori.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateUgovorDTO(
        @NotBlank(message = "Kupac ne moze biti prazan") String kupac,
        @NotNull(message = "Morate unijeti datum: rokIsporuke") LocalDate rokIsporuke
) {
}
