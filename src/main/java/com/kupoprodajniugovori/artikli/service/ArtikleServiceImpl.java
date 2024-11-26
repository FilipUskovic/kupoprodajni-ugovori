package com.kupoprodajniugovori.artikli.service;

import com.kupoprodajniugovori.artikli.Artikl;
import com.kupoprodajniugovori.artikli.ArtiklMapper;
import com.kupoprodajniugovori.artikli.ArtiklRepository;
import com.kupoprodajniugovori.artikli.dto.ArtiklDTO;
import com.kupoprodajniugovori.artikli.dto.CreateArtiklDTO;
import com.kupoprodajniugovori.ugovori.Ugovor;
import com.kupoprodajniugovori.ugovori.UgovorRepository;
import com.kupoprodajniugovori.utils.Status;
import com.kupoprodajniugovori.utils.exception.InvalidStatusException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtikleServiceImpl implements ArtiklService{

    //TODO: dodat kesiranje

    private final ArtiklRepository artiklRepository;
    private final UgovorRepository ugovorRepository;

    public ArtikleServiceImpl(ArtiklRepository artiklRepository, UgovorRepository ugovorRepository) {
        this.artiklRepository = artiklRepository;
        this.ugovorRepository = ugovorRepository;
    }


    @Override
    public List<ArtiklDTO> findArtikleByUgovorId(Long ugovorId) {
        if (!ugovorRepository.existsById(ugovorId)) {
            throw new EntityNotFoundException("Ugovor s ID-em " + ugovorId + " nije pronađen.");
        }
        return artiklRepository.findByUgovorIdAndUgovorObrisanFalse(ugovorId)
                .stream().map(ArtiklMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArtiklDTO> findAllArtikle(Pageable pageable) {
        Page<ArtiklDTO> artikliPage = artiklRepository.findAllByUgovorObrisanFalse(pageable)
                .map(ArtiklMapper::toDto);

        if (artikliPage.getTotalPages() > 0 && pageable.getPageNumber() >= artikliPage.getTotalPages()) {
            throw new IllegalArgumentException(
                    String.format("Tražena stranica %d ne postoji. Ukupan broj stranica: %d",
                            pageable.getPageNumber(), artikliPage.getTotalPages())
            );
        }

        return artikliPage.getContent();
    }


    @Override
    @Transactional
    public ArtiklDTO createArtikl(Long ugovorId, CreateArtiklDTO createArtiklDTO) {
        Ugovor ugovor = ugovorRepository.findByIdAndObrisanFalse(ugovorId)
                .orElseThrow(() -> new EntityNotFoundException("Ugovor nije pronađen, ID: " + ugovorId));
        validateUgovorStatusForArtiklCreation(ugovor);
        Artikl artikl = ArtiklMapper.toEntity(createArtiklDTO);
        artikl.setStatus(Status.KREIRANO);
        ugovor.addArtikl(artikl);
        return ArtiklMapper.toDto(artiklRepository.save(artikl));
    }



    @Override
    @Transactional
    public void deleteArtikl(Long ugovorId, Long artiklId) {
        Ugovor ugovor = ugovorRepository.findByIdAndObrisanFalse(ugovorId)
                .orElseThrow(() -> new EntityNotFoundException("Ugovor nije pronađen, ID: " + ugovorId));

        validateUgovorStatusForArtiklDeletion(ugovor);
    Artikl artikl = artiklRepository.findById(artiklId)
            .orElseThrow(() -> new EntityNotFoundException("Artikl nije pronađen, ID: " + artiklId));
        if (!artikl.getUgovor().getId().equals(ugovorId)) {
            throw new ValidationException("Artikl ne pripada ovom ugovoru");
        }

        ugovor.removeArtikl(artikl);
        artiklRepository.delete(artikl);

    }

    @Override
    @Transactional
    public void updateStatusForUgovor(Long ugovorId, Status status) {
        artiklRepository.updateStatusForUgovor(ugovorId,status);
    }

    private void validateUgovorStatusForArtiklCreation(Ugovor ugovor) {
        if (ugovor.getStatus() != Status.KREIRANO) {
            throw new InvalidStatusException("Artikli se mogu dodavati samo na ugovore u statusu KREIRANO");
        }
    }

    private void validateUgovorStatusForArtiklDeletion(Ugovor ugovor) {
        if (ugovor.getStatus() != Status.KREIRANO) {
            throw new InvalidStatusException("Artikli se mogu brisati samo s ugovora u statusu KREIRANO");
        }
    }
}
