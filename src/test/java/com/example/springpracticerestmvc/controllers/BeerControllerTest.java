package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.model.Beer;
import com.example.springpracticerestmvc.services.BeerService;
import com.example.springpracticerestmvc.services.impl.BeerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BeerService beerService;

    BeerServiceImpl beerServiceImpl;

    @Autowired
    ObjectMapper objectMapper;
    // for serializing-deserializing json object to pojo and vice versa

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<Beer> beerArgumentCaptor;

    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void getBeerById() throws Exception {

        var beer = beerServiceImpl.listBeers().getFirst();

        //given(beerService.getBeerById(any(UUID.class))).willReturn(beer);
        given(beerService.getBeerById(beer.getId())).willReturn(beer);

        // using json path matcher to access json object
        // such as $.id or $.beerName
        mockMvc.perform(get("/api/v1/beer/" + beer.getId())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(beer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(beer.getBeerName())));
    }

    @Test
    void test_list_beers() throws Exception {
        given(beerService.listBeers()).willReturn(beerServiceImpl.listBeers());

        mockMvc.perform(get("/api/v1/beer")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void test_create_new_beer() throws Exception {
        var beer = beerServiceImpl.listBeers().getFirst();
        beer.setId(null);
        beer.setVersion(null);

        given(beerService.saveNewBeer(any(Beer.class)))
            .willReturn(beerServiceImpl.listBeers().get(1));

        mockMvc.perform(
            post("/api/v1/beer")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(beer))
        ).andExpect(status().isCreated())
        .andExpect(header().exists("Location"));

        // writing to json
        //System.out.println(objectMapper.writeValueAsString(beer));

    }

    @Test
    void test_update_beer() throws Exception {
        var beer = beerServiceImpl.listBeers().getFirst();

        mockMvc.perform(
                put("/api/v1/beer/" + beer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer))
        ).andExpect(status().isNoContent());

        verify(beerService).updateBeerById(any(UUID.class), any(Beer.class));
    }

    @Test
    void test_delete_beer() throws Exception {
        var beer = beerServiceImpl.listBeers().getFirst();

        mockMvc.perform(
                delete("/api/v1/beer/" + beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(beerService).deleteById(uuidArgumentCaptor.capture());
        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());

    }

    @Test
    void test_patch_beer() throws Exception {
        var beer = beerServiceImpl.listBeers().getFirst();

        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName", "New Name");

        mockMvc.perform(
                patch("/api/v1/beer/" + beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap))
        ).andExpect(status().isNoContent());

        verify(beerService).patchBeerById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());
        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(beerMap.get("beerName")).isEqualTo(beerArgumentCaptor.getValue().getBeerName());
    }

}