package com.kupoprodajniugovori.artikli;

import com.kupoprodajniugovori.artikli.dto.ArtiklDTO;
import com.kupoprodajniugovori.artikli.dto.CreateArtiklDTO;
import org.springframework.stereotype.Component;

@Component
public class ArtiklMapper {

    public static ArtiklDTO toDto(Artikl artikl) {
        return new ArtiklDTO(
                artikl.getId(),
                artikl.getNaziv(),
                artikl.getDobavljac(),
                artikl.getKolicina(),
                artikl.getStatus()
        );
    }

    public static Artikl toEntity(CreateArtiklDTO dto) {
        Artikl artikl = new Artikl();
        artikl.setNaziv(dto.naziv());
        artikl.setDobavljac(dto.dobavljac());
        artikl.setKolicina(dto.kolicina());
        return artikl;
    }
}
