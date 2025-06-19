package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.model.BeerOrderCreateDTO;
import com.example.springpracticerestmvc.model.BeerOrderLineCreateDTO;
import com.example.springpracticerestmvc.repositories.BeerOrderRepository;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.example.springpracticerestmvc.repositories.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static com.example.springpracticerestmvc.controllers.BeerControllerTest.jwtRequestPostProcessor;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class BeerOrderControllerIT {

    @Autowired
    WebApplicationContext wac;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void test_list_beer_orders() throws Exception {
        mockMvc.perform(
                        get(BeerOrderController.BEER_ORDER_PATH)
                                .with(jwtRequestPostProcessor)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", greaterThan(0)));
    }

    @Test
    void test_get_beer_order_by_id() throws Exception {

        var beerOrder = beerOrderRepository.findAll().getFirst();

        mockMvc.perform(
                        get(BeerOrderController.BEER_ORDER_PATH_ID, beerOrder.getId())
                                .with(jwtRequestPostProcessor)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(beerOrder.getId().toString())));
    }

    @Test
    void test_create_beer_order() throws Exception {
        var customer = customerRepository.findAll().getFirst();
        var beer = beerRepository.findAll().getFirst();

        var beerOrderCreateDTO = BeerOrderCreateDTO.builder()
                .customerId(customer.getId())
                .beerOrderLines(
                        Set.of(
                                BeerOrderLineCreateDTO.builder()
                                        .beerId(beer.getId())
                                        .orderQuantity(1)
                                        .build()
                        )
                )
                .build();

        mockMvc.perform(
                        post(BeerOrderController.BEER_ORDER_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerOrderCreateDTO))
                                .with(jwtRequestPostProcessor)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
}