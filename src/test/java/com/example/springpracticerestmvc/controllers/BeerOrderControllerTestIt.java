package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.repositories.BeerOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.example.springpracticerestmvc.controllers.BeerControllerTest.jwtRequestPostProcessor;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerOrderControllerTestIt {

    @Autowired
    WebApplicationContext wac;

    @Autowired
    BeerOrderRepository beerOrderRepository;

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
                .andExpect(jsonPath("$.id", is(1)));
    }
}