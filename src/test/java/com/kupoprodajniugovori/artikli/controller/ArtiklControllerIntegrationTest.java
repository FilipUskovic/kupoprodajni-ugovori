package com.kupoprodajniugovori.artikli.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kupoprodajniugovori.artikli.Artikl;
import com.kupoprodajniugovori.artikli.ArtiklRepository;
import com.kupoprodajniugovori.artikli.dto.CreateArtiklDTO;
import com.kupoprodajniugovori.ugovori.Ugovor;
import com.kupoprodajniugovori.ugovori.UgovorRepository;
import com.kupoprodajniugovori.utils.Status;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ArtiklControllerIntegrationTest {

    //TODO: tranzakcije i rollbak nam ne treba ovdje posto redim s testcontainjerom ali je dobra praksa pisati

    @Container
    private static final PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>("postgres:15.2");

    @Autowired
    private MockMvc mockMvc;

   @Autowired
   UgovorRepository ugovorRepository;

    @Autowired
    ArtiklRepository artiklRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDB::getUsername);
        registry.add("spring.datasource.password", postgresDB::getPassword);
    }

    @BeforeAll
    static void setup() {
        postgresDB.start();
    }

    @BeforeEach
    @Transactional
    void init() {
        Ugovor ugovor = new Ugovor();
        ugovor.setBrojUgovora("UG-12345");
        ugovor.setKupac("Kupac d.o.o.");
        ugovor.setDatumAkontacije(java.time.LocalDate.now());
        ugovor.setRokIsporuke(java.time.LocalDate.now().plusDays(30));
        ugovor.setStatus(Status.valueOf("KREIRANO"));
        ugovorRepository.save(ugovor);


        Artikl artikl = new Artikl();
        artikl.setNaziv("Test Artikl");
        artikl.setDobavljac("Dobavljac d.o.o.");
        artikl.setKolicina(10);
        artikl.setStatus(Status.valueOf("KREIRANO"));
        artikl.setUgovor(ugovor);
        artikl.setUgovor(ugovor);
        artiklRepository.save(artikl);
    }


    @Test
    @WithMockUser(roles = {"USER"})
    @Transactional
    public void shouldReturnAllArtikliForUgovor() throws Exception {
        Long ugovorId = ugovorRepository.findAll().getFirst().getId();

        mockMvc.perform(get("/api/artikli")
                        .param("ugovorId", ugovorId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    @Transactional
    public void shouldCreateNewArtikl() throws Exception {
        Long ugovorId = ugovorRepository.findAll().getFirst().getId();
        CreateArtiklDTO createArtiklDTO = new CreateArtiklDTO("Novi Artikl", "Dobavljac d.o.o.", 5);
        String artiklJson = objectMapper.writeValueAsString(createArtiklDTO);

        mockMvc.perform(post("/api/artikli")
                        .param("ugovorId", ugovorId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(artiklJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.naziv", is(createArtiklDTO.naziv())));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Transactional
    public void shouldDeleteArtikl() throws Exception {
        Ugovor ugovor = ugovorRepository.findAll().getFirst();
        Artikl artikl = artiklRepository.findAll().getFirst();

        mockMvc.perform(delete("/api/artikli/{id}", artikl.getId())
                        .param("ugovorId", ugovor.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    @Transactional
    public void shouldReturn404WhenUgovorNotFound() throws Exception {
        Long nonExistentUgovorId = 999L;

        mockMvc.perform(get("/api/artikli")
                        .param("ugovorId", nonExistentUgovorId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println("Response Body: " + result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ugovor s ID-em " + nonExistentUgovorId + " nije pronađen."));
    }


}