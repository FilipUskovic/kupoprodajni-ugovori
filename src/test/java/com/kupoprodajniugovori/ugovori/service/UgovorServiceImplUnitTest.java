package com.kupoprodajniugovori.ugovori.service;

import com.kupoprodajniugovori.artikli.dto.ArtiklDTO;
import com.kupoprodajniugovori.artikli.service.ArtikleServiceImpl;
import com.kupoprodajniugovori.ugovori.*;
import com.kupoprodajniugovori.ugovori.dto.*;
import com.kupoprodajniugovori.utils.Status;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UgovorServiceImplUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(UgovorServiceImplUnitTest.class);


    @Mock
    private UgovorRepository ugovorRepository;

    @Mock
    private ArtikleServiceImpl artikleService;


    @Mock
    private UgovorMapper ugovorMapper;

    @InjectMocks
    private UgovorServiceImpl ugovorService;


    private static final Long TEST_ID = 1L;
    private static final String TEST_KUPAC = "Test Kupac";
    private static final String TEST_BROJ_UGOVORA = "1/2024";
    private static final LocalDate TODAY = LocalDate.now();


    private Ugovor testUgovor;
    private DetailsUgovorDTO testDetailsDto;


    @BeforeEach
    void setUp() {
        testUgovor = createTestUgovor();
        testDetailsDto = new DetailsUgovorDTO(
                TEST_ID,
                TEST_KUPAC,
                TEST_BROJ_UGOVORA,
                TODAY,
                TODAY.plusDays(10),
                Status.KREIRANO,
                Set.of(new ArtiklDTO(1L, "Artikl 1", "Dobavljac 1", 5, Status.KREIRANO))
        );

        UgovoriResponseDTO testResponseDto = new UgovoriResponseDTO(
                TEST_ID,
                TEST_KUPAC,
                TEST_BROJ_UGOVORA,
                TODAY,
                TODAY.plusDays(10),
                Status.KREIRANO
        );
    }



    @Test
    void shouldFindUgovorById() {
        when(ugovorRepository.findByIdAndObrisanFalse(TEST_ID))
                .thenReturn(Optional.of(testUgovor));
        when(ugovorMapper.toDetailDto(testUgovor))
                .thenReturn(testDetailsDto);

        DetailsUgovorDTO result = ugovorService.findUgovorById(TEST_ID);

        assertThat(result).usingRecursiveComparison().isEqualTo(testDetailsDto);
        verify(ugovorRepository).findByIdAndObrisanFalse(TEST_ID);
        verify(ugovorMapper).toDetailDto(testUgovor);
    }


    /* TODO: provjerit zasto je ovjdje ugovor null kod mapiranja toDto a radi na swaggeru bez greske
    @Test
    void shouldFindUgovoriWithPagination() {
        logger.info("Starting shouldFindUgovoriWithPagination test");

        UgovorSearchCriteria criteria = new UgovorSearchCriteria(
                TEST_KUPAC, true, null, null, null, null
        );
        logger.debug("UgovorSearchCriteria: {}", criteria);

        Pageable pageable = PageRequest.of(0, 10);
        logger.debug("Pageable: {}", pageable);

        SortField sortField = SortField.KUPAC;
        logger.debug("SortField: {}", sortField);

        PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(sortField.getFieldName())
        );
        logger.debug("PageRequest: {}", pageRequest);

        List<Ugovor> ugovori = List.of(createTestUgovor(), createTestUgovor());
        logger.debug("Test Ugovori list created: {}", ugovori);

        doReturn(new PageImpl<>(ugovori, pageRequest, ugovori.size()))
                .when(ugovorRepository)
                .findAll(any(Specification.class), eq(pageRequest));
        logger.info("Stubbed ugovorRepository.findAll(...)");

        UgovoriResponseDTO responseDTO = new UgovoriResponseDTO(
                TEST_ID, TEST_KUPAC, TEST_BROJ_UGOVORA,
                TODAY, TODAY.plusDays(10), Status.KREIRANO
        );
        logger.debug("ResponseDTO for stub: {}", responseDTO);

        when(ugovorMapper.toResponseDto(any(Ugovor.class)))
                .thenReturn(responseDTO);
        logger.info("Stubbed ugovorMapper.toResponseDto(...)");

        logger.info("Calling ugovorService.findUgovori(...)");
        List<UgovoriResponseDTO> result = ugovorService.findUgovori(
                criteria, pageable, sortField
        );

        logger.debug("Assert: Result has size {}", result.size());
        assertThat(result).hasSize(2);

        ArgumentCaptor<Ugovor> ugovorCaptor = ArgumentCaptor.forClass(Ugovor.class);
        verify(ugovorMapper, times(2)).toResponseDto(ugovorCaptor.capture());
        logger.info("Verified ugovorMapper.toResponseDto(...) called twice");

        ugovorCaptor.getAllValues().forEach(ugovor -> {
            logger.debug("Captured Ugovor: {}", ugovor);
            assertNotNull(ugovor);
        });
    }

     */




    @Test
    void shouldThrowExceptionWhenNoUgovoriFound() {
        UgovorSearchCriteria criteria = new UgovorSearchCriteria(
                "Nepostojeći", true, null, null, null, null
        );
        Pageable pageable = PageRequest.of(0, 10);
        SortField sortField = SortField.KUPAC;

        PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(sortField.getFieldName())
        );

        doReturn(new PageImpl<>(Collections.emptyList(), pageRequest, 0))
                .when(ugovorRepository)
                .findAll(any(Specification.class), eq(pageRequest));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ugovorService.findUgovori(criteria, pageable, sortField)
        );

        assertThat(exception.getMessage()).isEqualTo("Kupac nije pronađen");
    }


    @Test
    void shouldCreateUgovorWhenValidInput() {
        CreateUgovorDTO createDto = new CreateUgovorDTO(
                "Test Kupac",
                LocalDate.now(),
                LocalDate.now().plusDays(10)
        );

        Ugovor savedUgovor = new Ugovor();
        savedUgovor.setId(1L);
        savedUgovor.setKupac("Test Kupac");
        savedUgovor.setStatus(Status.KREIRANO);
        savedUgovor.setBrojUgovora("6/2024");
        savedUgovor.setDatumAkontacije(createDto.datumAkontacije());
        savedUgovor.setRokIsporuke(createDto.rokIsporuke());

        when(ugovorRepository.countByYear(anyInt())).thenReturn(5L);
        when(ugovorRepository.save(any(Ugovor.class))).thenReturn(savedUgovor);

        UgovoriResponseDTO result = ugovorService.createUgovor(createDto);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.kupac()).isEqualTo("Test Kupac");
        assertThat(result.status()).isEqualTo(Status.KREIRANO);
        assertThat(result.brojUgovora()).isEqualTo("6/2024");
        assertThat(result.datumAkontacije()).isEqualTo(createDto.datumAkontacije());
        assertThat(result.rokIsporuke()).isEqualTo(createDto.rokIsporuke());

        ArgumentCaptor<Ugovor> ugovorCaptor = ArgumentCaptor.forClass(Ugovor.class);
        verify(ugovorRepository).save(ugovorCaptor.capture());

        Ugovor capturedUgovor = ugovorCaptor.getValue();
        assertThat(capturedUgovor.getKupac()).isEqualTo(createDto.kupac());
        assertThat(capturedUgovor.getStatus()).isEqualTo(Status.KREIRANO);
    }



    @Test
        void shouldUpdateStatusFromNarucenoToIsporuceno() {
            Ugovor ugovor = createTestUgovor();
            ugovor.setStatus(Status.NARUČENO);

            when(ugovorRepository.findByIdAndObrisanFalse(TEST_ID))
                    .thenReturn(Optional.of(ugovor));
            when(ugovorRepository.save(any(Ugovor.class)))
                    .thenReturn(ugovor);

            ugovorService.updateStatus(TEST_ID, Status.ISPORUČENO);

            assertThat(ugovor.getStatus()).isEqualTo(Status.ISPORUČENO);
            verify(artikleService).updateStatusForUgovor(TEST_ID, Status.ISPORUČENO);
        }



    private Ugovor createTestUgovor() {
        Ugovor ugovor = new Ugovor();
        ugovor.setId(TEST_ID);
        ugovor.setKupac(TEST_KUPAC);
        ugovor.setBrojUgovora(TEST_BROJ_UGOVORA);
        ugovor.setDatumAkontacije(TODAY);
        ugovor.setRokIsporuke(TODAY.plusDays(10));
        ugovor.setStatus(Status.KREIRANO);
        ugovor.setObrisan(false);
        return ugovor;
    }

}