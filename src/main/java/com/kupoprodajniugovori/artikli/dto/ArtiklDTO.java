package com.kupoprodajniugovori.artikli.dto;

import com.kupoprodajniugovori.utils.Status;

public record ArtiklDTO(
        Long id,
        String naziv,
        String dobavljac,
        Integer kolicina,
        Status status
) {
}
