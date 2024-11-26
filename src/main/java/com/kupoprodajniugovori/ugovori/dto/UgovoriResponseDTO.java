package com.kupoprodajniugovori.ugovori.dto;

import com.kupoprodajniugovori.utils.Status;

import java.time.LocalDate;

public record UgovoriResponseDTO(
    Long id,
    String kupac,
    String brojUgovora,
    LocalDate datumAkontacije,
    LocalDate rokIsporuke,
    Status status
) {
}
