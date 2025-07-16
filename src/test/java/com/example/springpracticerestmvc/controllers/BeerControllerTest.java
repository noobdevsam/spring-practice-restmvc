package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.config.SecConfig;
import com.example.springpracticerestmvc.model.BeerDTO;
import com.example.springpracticerestmvc.services.BeerService;
import com.example.springpracticerestmvc.services.impl.BeerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the BeerController class.
 * This class uses Spring's WebMvcTest to test the BeerController endpoints.
 */
@WebMvcTest(BeerController.class)
@Import(SecConfig.class)
class BeerControllerTest {

    /**
     * JWT request post-processor for authentication in tests.
     */
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
            jwt()
                    .jwt(jwt -> {
                        jwt.subject("messaging-client");
                        jwt.claim("scope", "message-read");
                        jwt.claim("scope", "message-write");
                        jwt.notBefore(Instant.now().minusSeconds(51));
                    });

    @Autowired
    MockMvc mockMvc; // MockMvc for simulating HTTP requests in tests.

    @MockitoBean
    BeerService beerService; // Mocked BeerService for dependency injection.

    BeerServiceImpl beerServiceImpl; // Actual implementation of BeerService for test data.

    @Autowired
    ObjectMapper objectMapper; // ObjectMapper for JSON serialization/deserialization.

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor; // Captor for UUID arguments.

    @Captor
    ArgumentCaptor<BeerDTO> beerArgumentCaptor; // Captor for BeerDTO arguments.

    /**
     * Setup method to initialize test data before each test.
     */
    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }

    /**
     * Test for retrieving a beer by its ID.
     * Verifies the response contains the correct beer details.
     */
    @Test
    void getBeerById() throws Exception {
        var beer = beerServiceImpl.listBeers(null, null, false, 1, 25)
                .getContent().getFirst();

        given(beerService.getBeerById(beer.getId())).willReturn(
                Optional.of(beer)
        );

        mockMvc.perform(get(BeerController.BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor) // for authentication
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(beer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(beer.getBeerName())));
    }

    /**
     * Test for listing beers.
     * Verifies the response contains the correct number of beers.
     */
    @Test
    void test_list_beers() throws Exception {
        given(beerService.listBeers(any(), any(), any(), any(), any()))
                .willReturn(beerServiceImpl.listBeers(null, null, false, null, null));

        mockMvc.perform(get(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor) // for authentication
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()", is(3)));
    }

    /**
     * Test for creating a new beer.
     * Verifies the response contains a "Location" header.
     */
    @Test
    void test_create_new_beer() throws Exception {
        var beer = beerServiceImpl.listBeers(null, null, false, 1, 25)
                .getContent().getFirst();
        beer.setVersion(null);

        given(beerService.saveNewBeer(any(BeerDTO.class)))
                .willReturn(beerServiceImpl.listBeers(null, null, false, 1, 25)
                        .getContent().getFirst());

        mockMvc.perform(
                        post(BeerController.BEER_PATH)
                                .with(jwtRequestPostProcessor) // for authentication
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beer))
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    /**
     * Test for updating a beer by its ID.
     * Verifies the response status is "No Content".
     */
    @Test
    void test_update_beer() throws Exception {
        var beer = beerServiceImpl.listBeers(null, null, false, 1, 25)
                .getContent().getFirst();

        given(beerService.updateBeerById(any(), any())).willReturn(Optional.of(beer));
        mockMvc.perform(
                put(BeerController.BEER_PATH_ID, beer.getId())
                        .with(jwtRequestPostProcessor) // for authentication
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer))
        ).andExpect(status().isNoContent());

        verify(beerService).updateBeerById(any(UUID.class), any(BeerDTO.class));
    }

    /**
     * Test for deleting a beer by its ID.
     * Verifies the response status is "No Content".
     */
    @Test
    void test_delete_beer() throws Exception {
        var beer = beerServiceImpl.listBeers(null, null, false, 1, 25)
                .getContent().getFirst();

        given(beerService.deleteById(any())).willReturn(true);

        mockMvc.perform(
                delete(BeerController.BEER_PATH_ID, beer.getId())
                        .with(jwtRequestPostProcessor) // for authentication
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(beerService).deleteById(uuidArgumentCaptor.capture());
        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }

    /**
     * Test for patching a beer by its ID.
     * Verifies the response status is "No Content".
     */
    @Test
    void test_patch_beer() throws Exception {
        var beer = beerServiceImpl.listBeers(null, null, false, 1, 25)
                .getContent().getFirst();

        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName", "New Name");

        mockMvc.perform(
                patch(BeerController.BEER_PATH_ID, beer.getId())
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap))
        ).andExpect(status().isNoContent());

        verify(beerService).patchBeerById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());
        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(beerMap.get("beerName")).isEqualTo(beerArgumentCaptor.getValue().getBeerName());
    }

    /**
     * Test for retrieving a beer by its ID when the beer is not found.
     * Verifies the response status is "Not Found".
     */
    @Test
    void test_get_beer_by_id_not_found() throws Exception {
        given(beerService.getBeerById(any(UUID.class)))
                .willReturn(Optional.empty());
        mockMvc.perform(
                get(BeerController.BEER_PATH_ID, UUID.randomUUID())
                        .with(jwtRequestPostProcessor) // for authentication
        ).andExpect(status().isNotFound());
    }

    /**
     * Test for creating a beer with a null beerName.
     * Verifies the response status is "Bad Request".
     */
    @Test
    void test_create_beer_null_beerName() throws Exception {
        var beerDTO = new BeerDTO();

        given(beerService.saveNewBeer(any(BeerDTO.class)))
                .willReturn(beerServiceImpl.listBeers(null, null, false, 1, 25)
                        .getContent().getFirst());

        MvcResult mvcResult = mockMvc.perform(
                        post(BeerController.BEER_PATH)
                                .with(jwtRequestPostProcessor) // for authentication
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDTO))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(6)))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    /**
     * Test for updating a beer with a blank beerName.
     * Verifies the response status is "Bad Request".
     */
    @Test
    void test_update_beer_blank_name() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25)
                .getContent().getFirst();
        beer.setBeerName("");

        given(beerService.updateBeerById(any(), any()))
                .willReturn(Optional.of(beer));

        mockMvc.perform(
                        put(BeerController.BEER_PATH_ID, beer.getId())
                                .with(jwtRequestPostProcessor) // for authentication
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(beer)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }
}