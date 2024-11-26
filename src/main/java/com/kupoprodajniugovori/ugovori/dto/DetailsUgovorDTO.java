package com.kupoprodajniugovori.ugovori.dto;

import com.kupoprodajniugovori.artikli.dto.ArtiklDTO;
import com.kupoprodajniugovori.utils.Status;

import java.time.LocalDate;
import java.util.Set;

public record DetailsUgovorDTO(
        Long id,
        String kupac,
        String brojUgovora,
        LocalDate datumAkontacije,
        LocalDate rokIsporuke,
        Status status,
        Set<ArtiklDTO> artikli

) {
}
