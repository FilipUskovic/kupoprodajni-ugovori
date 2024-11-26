package com.kupoprodajniugovori.ugovori.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kupoprodajniugovori.ugovori.Ugovor;
import com.kupoprodajniugovori.ugovori.UgovorRepository;
import com.kupoprodajniugovori.ugovori.dto.CreateUgovorDTO;
import com.kupoprodajniugovori.ugovori.dto.UpdateUgovorDTO;
import com.kupoprodajniugovori.utils.Status;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UgovorControllerIntegrationTest {

    //TODO: potrebno je testirati vise usecase-va i validacije bolje ovo su samo osnovni primjeri

    @Container
    private static final PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>("postgres:15.2");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UgovorRepository ugovorRepository;

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
        ugovor.setBrojUgovora("UG-54321");
        ugovor.setKupac("Kupac Test d.o.o.");
        ugovor.setDatumAkontacije(LocalDate.now());
        ugovor.setRokIsporuke(LocalDate.now().plusDays(60));
        ugovor.setStatus(Status.valueOf("KREIRANO"));
        ugovorRepository.save(ugovor);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    @Transactional
    public void shouldCreateNewUgovor() throws Exception {
        CreateUgovorDTO createUgovorDTO = new CreateUgovorDTO("Novi Kupac", LocalDate.now(), LocalDate.now().plusDays(45));
        String ugovorJson = objectMapper.writeValueAsString(createUgovorDTO);

        mockMvc.perform(post("/api/ugovori")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ugovorJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kupac", is(createUgovorDTO.kupac())));
    }


    @Test
    @WithMockUser(roles = {"USER"})
    @Transactional
    public void shouldReturnUgovorById() throws Exception {
        Long ugovorId = ugovorRepository.findAll().getFirst().getId();

        mockMvc.perform(get("/api/ugovori/{id}", ugovorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ugovorId.intValue())));
    }


    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    @Transactional
    public void shouldUpdateUgovor() throws Exception {
        Long ugovorId = ugovorRepository.findAll().getFirst().getId();
        UpdateUgovorDTO updateUgovorDTO = new UpdateUgovorDTO("UG-98765", LocalDate.now().plusDays(60));
        String updateJson = objectMapper.writeValueAsString(updateUgovorDTO);

        String expectedRokIsporuke = updateUgovorDTO.rokIsporuke().toString();

        mockMvc.perform(put("/api/ugovori/{id}", ugovorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kupac", is(updateUgovorDTO.kupac())))
                .andExpect(jsonPath("$.rokIsporuke", is(expectedRokIsporuke)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    @Transactional
    public void shouldReturn404WhenUgovorNotFound() throws Exception {
        Long nonExistentUgovorId = 999L;

        mockMvc.perform(get("/api/ugovori/{id}", nonExistentUgovorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Ugovor sa id-em " + nonExistentUgovorId + " nije pronaden")));
    }
}
