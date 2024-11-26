package com.kupoprodajniugovori.ugovori.service;

import com.kupoprodajniugovori.artikli.service.ArtikleServiceImpl;
import com.kupoprodajniugovori.ugovori.*;
import com.kupoprodajniugovori.ugovori.dto.*;
import com.kupoprodajniugovori.utils.Status;
import com.kupoprodajniugovori.utils.exception.InvalidStatusException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UgovorServiceImpl implements UgovorService {

    //TODO: dodat kesiranje

    private final UgovorRepository ugovorRepository;
    private final UgovorMapper ugovorMapper;
    private final ArtikleServiceImpl artikleService;

    public UgovorServiceImpl(UgovorRepository ugovorRepository, UgovorMapper ugovorMapper, ArtikleServiceImpl artikleService) {
        this.ugovorRepository = ugovorRepository;
        this.ugovorMapper = ugovorMapper;
        this.artikleService = artikleService;
    }



    private static final int DEFAULT_PAGE_SIZE = 10;


    @Override
    public List<UgovoriResponseDTO> findUgovori(UgovorSearchCriteria criteria, Pageable pageable, SortField sortField) {
        PageRequest pageRequest = createPageRequest(pageable, sortField);

        List<Ugovor> ugovori = ugovorRepository.findAll(
                UgovorSpecification.createSpecification(criteria),
                pageRequest
        ).getContent();

        if (ugovori.isEmpty()) {
            throw new EntityNotFoundException("Kupac nije pronađen");
        }

       // TODO: Maknuti ovo je samo za test
        ugovori.forEach(ugovor -> {
            if (ugovor == null) {
                System.err.println("Jedan od ugovora iz repo-a je null!");
            }
        });

        return ugovori.stream()
                .map(UgovorMapper::toResponseDto)
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public UgovoriResponseDTO createUgovor(CreateUgovorDTO createUgovorDTO) {
       validateCreateUgovor(createUgovorDTO);
       Ugovor ugovor = UgovorMapper.toEntity(createUgovorDTO);
       ugovor.setStatus(Status.KREIRANO);
       ugovor.setBrojUgovora(generateBrojUgovora());
       return UgovorMapper.toResponseDto(ugovorRepository.save(ugovor));
    }


    @Override
    @Transactional
    public UgovoriResponseDTO updateUgovor(Long id,UpdateUgovorDTO updateUgovor) {
        Ugovor ugovor = ugovorRepository.findByIdAndObrisanFalse(id).orElseThrow(
                () -> new EntityNotFoundException("Ugovor nije pronaden, ID; " + id)
        );
        validateUpdateUgovor(ugovor, updateUgovor);
        if(!isUgovorAktivan(ugovor.getStatus())){
            ugovor.setRokIsporuke(updateUgovor.rokIsporuke());
        }
        ugovor.setKupac(updateUgovor.kupac());
        return UgovorMapper.toResponseDto(ugovorRepository.save(ugovor));
    }




    @Override
    @Transactional
    public void deleteUgovor(Long id) {
        Ugovor ugovor = ugovorRepository.findByIdAndObrisanFalse(id).orElseThrow(
                () -> new EntityNotFoundException("Ugovor nije pronaden, ID; " + id));
        if(!ugovor.getStatus().equals(Status.KREIRANO)){
            throw new InvalidStatusException("Samo ugovori u statusu KREIRANO mogu biti obrisani");
        }
        ugovorRepository.softDelete(id);
    }

    @Override
    @Transactional
    public UgovoriResponseDTO updateStatus(Long id, Status newStats) {
        Ugovor ugovor = ugovorRepository.findByIdAndObrisanFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Ugovor nije pronađen, ID: " + id));
        validateStatus(ugovor.getStatus(), newStats);
        ugovor.setStatus(newStats);
        artikleService.updateStatusForUgovor(id, newStats);
        return UgovorMapper.toResponseDto(ugovorRepository.save(ugovor));
    }

    @Override
    public DetailsUgovorDTO findUgovorById(Long id) {
        return ugovorRepository.findByIdAndObrisanFalse(id)
                .map(ugovorMapper::toDetailDto).orElseThrow(()
                -> new EntityNotFoundException("Ugovor sa id-em " + id + " nije pronaden"));
    }



    private void validateCreateUgovor(CreateUgovorDTO createUgovorDTO) {
        if(createUgovorDTO.rokIsporuke().isBefore(createUgovorDTO.datumAkontacije())){
            throw new ValidationException("Rok isporuke ne može biti prije datuma akontacije");
        }
    }

    private void validateUpdateUgovor(Ugovor ugovor, UpdateUgovorDTO updateUgovor) {
        if(ugovor.getStatus().equals(Status.ISPORUČENO)){
            throw new InvalidStatusException("Nije moguće ažurirati isporučeni ugovor");
        }
    }

    private boolean isUgovorAktivan(Status status) {
        return status == Status.KREIRANO || status == Status.NARUČENO;

    }

    private void validateStatus(Status currentStatus, Status newStatus) {
        if (currentStatus == Status.KREIRANO && newStatus != Status.NARUČENO ||
                currentStatus == Status.NARUČENO && newStatus != Status.ISPORUČENO ||
                currentStatus == Status.ISPORUČENO) {
            throw new InvalidStatusException("Nedozvoljena promjena statusa");
        }
    }

    private String generateBrojUgovora() {
        int year = Year.now().getValue();
        long count = ugovorRepository.countByYear(year) + 1;
        return String.format("%d/%d", count, year);
    }


    private PageRequest createPageRequest(Pageable pageable, SortField sortField) {
        int pageNumber = pageable != null ? pageable.getPageNumber() : 0;
        int pageSize = pageable != null && pageable.getPageSize() > 0 ?
                pageable.getPageSize() : DEFAULT_PAGE_SIZE;
        Sort sort = sortField != null ?
                Sort.by(sortField.getFieldName()) :
                Sort.by("kupac");

        return PageRequest.of(pageNumber, pageSize, sort);
    }


}
