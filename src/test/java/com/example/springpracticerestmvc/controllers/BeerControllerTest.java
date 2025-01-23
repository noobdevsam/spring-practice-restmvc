package com.example.springpracticerestmvc.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.springpracticerestmvc.model.Beer;
import com.example.springpracticerestmvc.services.BeerService;
import com.example.springpracticerestmvc.services.impl.BeerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;


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

    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void getBeerById() throws Exception {

        var beer = beerServiceImpl.listBeers().get(0);

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
        var beer = beerServiceImpl.listBeers().get(0);
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

}