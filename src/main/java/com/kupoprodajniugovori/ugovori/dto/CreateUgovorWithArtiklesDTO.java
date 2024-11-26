package com.kupoprodajniugovori.ugovori.dto;

import com.kupoprodajniugovori.artikli.dto.CreateArtiklDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record CreateUgovorWithArtiklesDTO(
       @NotBlank(message = "polje kupac ne moze biti prazno")
       String kupac,
       @NotNull(message = "Morate unijeti valjani datum akontacije")
       LocalDate datumAkontacije,
       @NotNull(message = "morate unijeti valjani rok isporuke")
       LocalDate rokIsporuke,
       @NotEmpty
       Set<CreateArtiklDTO> artikli
) {
}
