package com.kupoprodajniugovori.ugovori.controller;

import com.kupoprodajniugovori.ugovori.UgovorSearchCriteria;
import com.kupoprodajniugovori.ugovori.dto.*;
import com.kupoprodajniugovori.ugovori.service.UgovorServiceImpl;
import com.kupoprodajniugovori.utils.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Ugovori", description = "API za rad s ugovorima")
@RequestMapping("/api/ugovori")
@SecurityRequirement(name = "bearerAuth")
public class UgovorController {

    private final UgovorServiceImpl ugovorService;

    public UgovorController(UgovorServiceImpl ugovorService) {
        this.ugovorService = ugovorService;
    }

    @Operation(
            summary = "Dohvat ugovor-e po kriteri",
            description = "Dohvaća sve ugovore koji pripadajucim kriterima"
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<UgovoriResponseDTO>> getUgovori(
            @RequestParam(required = false) String kupac,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) SortField sort,
            @PageableDefault(size = 10) Pageable pageable) {

        UgovorSearchCriteria criteria = UgovorSearchCriteria.builder()
                .kupac(kupac)
                .active(active)
                .build();

        List<UgovoriResponseDTO> response = ugovorService.findUgovori(criteria, pageable, sort);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Dohvat ugovora po id",
            description = "Dohvaća sve ugovor koji pripadajucim odgovarjaučem id-u"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DetailsUgovorDTO> getUgovor(@PathVariable Long id) {
        return ResponseEntity.ok(ugovorService.findUgovorById(id));
    }

    @Operation(
            summary = "Kreira ugovor",
            description = "Kreiranje ugovora "
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UgovoriResponseDTO> createUgovor(
            @Valid @RequestBody CreateUgovorDTO createUgovorDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ugovorService.createUgovor(createUgovorDTO));
    }

    @Operation(
            summary = "Izmjena ugovor-a",
            description = "Izmjena napravljenoga ugovora "
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UgovoriResponseDTO> updateUgovor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUgovorDTO updateUgovorDTO) {
        return ResponseEntity.ok(ugovorService.updateUgovor(id, updateUgovorDTO));
    }

    @Operation(
            summary = "Izmjena Status-a",
            description = "Izmjena Statusa "
    )
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UgovoriResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam Status status) {
        return ResponseEntity.ok(ugovorService.updateStatus(id, status));
    }

    @Operation(
            summary = "Delete ugovor",
            description = " Soft Delete ugovora "
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUgovor(@PathVariable Long id) {
        ugovorService.deleteUgovor(id);
        return ResponseEntity.status(HttpStatus.OK).body("Ugovor s ID-em " + id + " uspješno obrisan.");
    }
}
