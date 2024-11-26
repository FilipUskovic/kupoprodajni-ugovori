package com.kupoprodajniugovori.artikli.service;

import com.kupoprodajniugovori.artikli.dto.ArtiklDTO;
import com.kupoprodajniugovori.artikli.dto.CreateArtiklDTO;
import com.kupoprodajniugovori.utils.Status;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArtiklService {
    List<ArtiklDTO> findArtikleByUgovorId(Long ugovorId);

    List<ArtiklDTO> findAllArtikle(Pageable pageable);

    ArtiklDTO createArtikl(Long ugovorId, CreateArtiklDTO createArtiklDTO);

    void deleteArtikl(Long ugovorId, Long artiklId);

    void updateStatusForUgovor(Long ugovorId, Status status);
}
