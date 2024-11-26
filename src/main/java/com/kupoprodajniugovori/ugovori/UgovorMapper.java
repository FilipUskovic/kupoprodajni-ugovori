package com.kupoprodajniugovori.ugovori;

import com.kupoprodajniugovori.artikli.Artikl;
import com.kupoprodajniugovori.artikli.ArtiklMapper;
import com.kupoprodajniugovori.ugovori.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UgovorMapper {
    private static final Logger logger = LoggerFactory.getLogger(UgovorMapper.class);


    private final ArtiklMapper artiklMapper;

    public UgovorMapper(ArtiklMapper artiklMapper) {
        this.artiklMapper = artiklMapper;
    }

    public static UgovoriResponseDTO toResponseDto(Ugovor ugovor) {
        logger.debug("Mapping Ugovor to UgovoriResponseDTO: {}", ugovor);

        if (ugovor == null) {
            logger.error("Ugovor is null in toResponseDto!");
            throw new NullPointerException("Ugovor is null");
        }
        return new UgovoriResponseDTO(
                ugovor.getId(),
                ugovor.getKupac(),
                ugovor.getBrojUgovora(),
                ugovor.getDatumAkontacije(),
                ugovor.getRokIsporuke(),
                ugovor.getStatus()
        );
    }

    public DetailsUgovorDTO toDetailDto(Ugovor ugovor) {
        return new DetailsUgovorDTO(
                ugovor.getId(),
                ugovor.getKupac(),
                ugovor.getBrojUgovora(),
                ugovor.getDatumAkontacije(),
                ugovor.getRokIsporuke(),
                ugovor.getStatus(),
                ugovor.getArtikli().stream()
                        .map(ArtiklMapper::toDto)
                        .collect(Collectors.toSet())
        );
    }

    public static Ugovor toEntity(CreateUgovorDTO dto) {
        Ugovor ugovor = new Ugovor();
        ugovor.setKupac(dto.kupac());
        ugovor.setDatumAkontacije(dto.datumAkontacije());
        ugovor.setRokIsporuke(dto.rokIsporuke());
        return ugovor;
    }

    public Ugovor toEntity(CreateUgovorWithArtiklesDTO dto) {
        Ugovor ugovor = new Ugovor();
        ugovor.setKupac(dto.kupac());
        ugovor.setDatumAkontacije(dto.datumAkontacije());
        ugovor.setRokIsporuke(dto.rokIsporuke());
        dto.artikli().forEach(artiklDto -> {
            Artikl artikl = ArtiklMapper.toEntity(artiklDto);
            ugovor.addArtikl(artikl);
        });

        return ugovor;
    }

    public static void updateEntity(Ugovor ugovor, UpdateUgovorDTO dto) {
        ugovor.setKupac(dto.kupac());
        ugovor.setRokIsporuke(dto.rokIsporuke());
    }


}
