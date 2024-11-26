package com.kupoprodajniugovori.artikli.controller;

import com.kupoprodajniugovori.artikli.dto.ArtiklDTO;
import com.kupoprodajniugovori.artikli.dto.CreateArtiklDTO;
import com.kupoprodajniugovori.artikli.service.ArtikleServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artikli")
@Tag(name = "Artikli", description = "API za upravljanje artiklima")
@SecurityRequirement(name = "bearerAuth")
public class ArtiklController {

    private final ArtikleServiceImpl artikleService;

    public ArtiklController(ArtikleServiceImpl artikleService) {
        this.artikleService = artikleService;
    }

    @Operation(
            summary = "Dohvat artikala za ugovor",
            description = "Dohvaća sve artikle koji pripadaju određenom ugovoru"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Uspješno dohvaćeni artikli",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArtiklDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ugovor nije pronađen"
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ArtiklDTO>> getArtikliByUgovor(
            @RequestParam(required = false) Long ugovorId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        Pageable validPageable = validatePageable(pageable);
        if (ugovorId != null) {
            // Ako je `ugovorId` zadan, dohvatite artikle za taj ugovor.
            List<ArtiklDTO> artikli = artikleService.findArtikleByUgovorId(ugovorId);
            return ResponseEntity.ok(artikli);
        } else {
            // Ako `ugovorId` nije zadan, vraćamo sve artikle s paginacijom.
            List<ArtiklDTO> artikliPage = artikleService.findAllArtikle(validPageable);
            return ResponseEntity.ok(artikliPage);
        }
    }

    @Operation(
            summary = "Kreiranje novog artikla",
            description = "Kreira novi artikl za određeni ugovor"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Artikl uspješno kreiran",
                    content = @Content(schema = @Schema(implementation = ArtiklDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nevažeći podaci"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ugovor nije pronađen"
            )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ArtiklDTO> createArtikl(
            @RequestParam Long ugovorId, @Valid @RequestBody CreateArtiklDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(artikleService.createArtikl(ugovorId, dto));
    }

    @Operation(
            summary = "Brisanje artikla",
            description = "Briše artikl iz određenog ugovora"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Artikl uspješno obrisan"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Artikl ili ugovor nisu pronađeni"
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteArtikl(@PathVariable Long id, @RequestParam Long ugovorId) {
        artikleService.deleteArtikl(ugovorId, id);
        return ResponseEntity.status(HttpStatus.OK).body("Artikl s ID-em " + id + " uspješno obrisan.");
    }


    private Pageable validatePageable(Pageable pageable) {
        List<String> validSortFields = List.of("id", "naziv", "dobavljac", "kolicina", "status");
        if (pageable.getSort().stream().anyMatch(order -> !validSortFields.contains(order.getProperty()))) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").ascending());
        }

        return pageable;
    }
}
