package com.kupoprodajniugovori.artikli.service;

import com.kupoprodajniugovori.artikli.Artikl;
import com.kupoprodajniugovori.artikli.ArtiklRepository;
import com.kupoprodajniugovori.artikli.dto.ArtiklDTO;
import com.kupoprodajniugovori.artikli.dto.CreateArtiklDTO;
import com.kupoprodajniugovori.ugovori.Ugovor;
import com.kupoprodajniugovori.ugovori.UgovorRepository;
import com.kupoprodajniugovori.utils.Status;
import com.kupoprodajniugovori.utils.exception.InvalidStatusException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtikleServiceImplUnitTest {

    @Mock
    private ArtiklRepository artiklRepository;

    @Mock
    private UgovorRepository ugovorRepository;

    @InjectMocks
    private ArtikleServiceImpl artiklService;

    @Test
    void shouldCreateArtiklWhenValidInput() {
        Long ugovorId = 1L;
        CreateArtiklDTO createDto = new CreateArtiklDTO(
                "Test Artikl",
                "Test Dobavljac",
                5
        );

        Ugovor ugovor = createTestUgovor(ugovorId);
        Artikl artikl = createTestArtikl(1L, createDto.naziv());
        artikl.setDobavljac(createDto.dobavljac());
        artikl.setKolicina(createDto.kolicina());

        when(ugovorRepository.findByIdAndObrisanFalse(ugovorId))
                .thenReturn(Optional.of(ugovor));
        when(artiklRepository.save(any(Artikl.class))).thenReturn(artikl);

        ArtiklDTO result = artiklService.createArtikl(ugovorId, createDto);

        assertThat(result.naziv()).isEqualTo(createDto.naziv());
        assertThat(result.dobavljac()).isEqualTo(createDto.dobavljac());
        assertThat(result.kolicina()).isEqualTo(createDto.kolicina());
    }

    @Test
    void shouldThrowExceptionWhenUgovorNotFoundForFind() {
        Long ugovorId = 1L;
        when(ugovorRepository.existsById(ugovorId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> artiklService.findArtikleByUgovorId(ugovorId));
    }

    @Test
    void shouldFindAllArtikleWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Artikl> artikli = List.of(
                createTestArtikl(1L, "Artikl 1"),
                createTestArtikl(2L, "Artikl 2")
        );
        Page<Artikl> artiklPage = new PageImpl<>(artikli, pageable, 2);

        when(artiklRepository.findAllByUgovorObrisanFalse(pageable))
                .thenReturn(artiklPage);

        List<ArtiklDTO> result = artiklService.findAllArtikle(pageable);

        assertThat(result).hasSize(2);
        verify(artiklRepository).findAllByUgovorObrisanFalse(pageable);
    }


@Test
void shouldReturnArticlesWhenPageExists() {
    Pageable pageable = PageRequest.of(0, 10); 
    List<Artikl> artikli = List.of(
            createTestArtikl(1L, "Artikl 1"),
            createTestArtikl(2L, "Artikl 2")
    );

    Page<Artikl> artiklPage = new PageImpl<>(
            artikli,
            pageable,
            15 
    );

    when(artiklRepository.findAllByUgovorObrisanFalse(pageable))
            .thenReturn(artiklPage);

    List<ArtiklDTO> result = artiklService.findAllArtikle(pageable);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).naziv()).isEqualTo("Artikl 1");
}

    @Test
    void shouldDeleteArtiklWhenValidRequest() {
        Long ugovorId = 1L;
        Long artiklId = 1L;

        Ugovor ugovor = createTestUgovor(ugovorId);
        Artikl artikl = createTestArtikl(artiklId, "Test Artikl");
        artikl.setUgovor(ugovor);

        when(ugovorRepository.findByIdAndObrisanFalse(ugovorId))
                .thenReturn(Optional.of(ugovor));
        when(artiklRepository.findById(artiklId))
                .thenReturn(Optional.of(artikl));

        artiklService.deleteArtikl(ugovorId, artiklId);

        verify(artiklRepository).delete(artikl);
    }

    @Test
    void shouldThrowExceptionWhenDeletingArtiklFromNonExistentUgovor() {
        Long ugovorId = 1L;
        Long artiklId = 1L;

        when(ugovorRepository.findByIdAndObrisanFalse(ugovorId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> artiklService.deleteArtikl(ugovorId, artiklId));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentArtikl() {
        Long ugovorId = 1L;
        Long artiklId = 1L;

        Ugovor ugovor = createTestUgovor(ugovorId);
        when(ugovorRepository.findByIdAndObrisanFalse(ugovorId))
                .thenReturn(Optional.of(ugovor));
        when(artiklRepository.findById(artiklId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> artiklService.deleteArtikl(ugovorId, artiklId));
    }

    @Test
    void shouldThrowExceptionWhenDeletingArtiklFromWrongUgovor() {
        Long ugovorId = 1L;
        Long wrongUgovorId = 2L;
        Long artiklId = 1L;

        Ugovor ugovor = createTestUgovor(wrongUgovorId);
        Artikl artikl = createTestArtikl(artiklId, "Test Artikl");
        artikl.setUgovor(ugovor);

        when(ugovorRepository.findByIdAndObrisanFalse(ugovorId))
                .thenReturn(Optional.of(createTestUgovor(ugovorId)));
        when(artiklRepository.findById(artiklId))
                .thenReturn(Optional.of(artikl));

        assertThrows(ValidationException.class,
                () -> artiklService.deleteArtikl(ugovorId, artiklId));
    }

    @Test
    void shouldUpdateStatusForUgovor() {
        Long ugovorId = 1L;
        Status newStatus = Status.NARUČENO;

        artiklService.updateStatusForUgovor(ugovorId, newStatus);

        verify(artiklRepository).updateStatusForUgovor(ugovorId, newStatus);
    }

    @Test
    void shouldThrowExceptionWhenCreatingArtiklForNonKreiranoStatus() {
        Long ugovorId = 1L;
        CreateArtiklDTO dto = new CreateArtiklDTO("Test", "Test", 1);

        Ugovor ugovor = createTestUgovor(ugovorId);
        ugovor.setStatus(Status.NARUČENO);

        when(ugovorRepository.findByIdAndObrisanFalse(ugovorId))
                .thenReturn(Optional.of(ugovor));

        assertThrows(InvalidStatusException.class,
                () -> artiklService.createArtikl(ugovorId, dto));
    }


    private Ugovor createTestUgovor(Long id) {
        Ugovor ugovor = new Ugovor();
        ugovor.setId(id);
        ugovor.setStatus(Status.KREIRANO);
        ugovor.setObrisan(false);
        return ugovor;
    }

    private Artikl createTestArtikl(Long id, String naziv) {
        Artikl artikl = new Artikl();
        artikl.setId(id);
        artikl.setNaziv(naziv);
        artikl.setDobavljac("Test Dobavljac");
        artikl.setKolicina(1);
        artikl.setStatus(Status.KREIRANO);
        return artikl;
    }
}
