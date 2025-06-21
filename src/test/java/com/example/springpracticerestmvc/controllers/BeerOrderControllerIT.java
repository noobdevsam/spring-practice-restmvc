package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.model.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static com.example.springpracticerestmvc.controllers.BeerControllerTest.jwtRequestPostProcessor;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Transactional
    @Test
    void test_update_beer_order() throws Exception {
        var beerOrder = beerOrderRepository.findAll().getFirst();
        Set<BeerOrderLineUpdateDTO> lines = new HashSet<>();

        beerOrder.getBeerOrderLines().forEach(beerOrderLine -> {
            lines.add(
                    BeerOrderLineUpdateDTO.builder()
                            .id(beerOrderLine.getId())
                            .beerId(beerOrderLine.getBeer().getId())
                            .orderQuantity(beerOrderLine.getOrderQuantity())
                            .quantityAllocated(beerOrderLine.getQuantityAllocated())
                            .build()
            );
        });

        var beerOrderUpdateDTO = BeerOrderUpdateDTO.builder()
                .customerId(beerOrder.getCustomer().getId())
                .customerRef("TestRef")
                .beerOrderLines(lines)
                .beerOrderShipment(
                        BeerOrderShipmentUpdateDTO.builder()
                                .trackingNumber("123654")
                                .build()
                )
                .build();

        mockMvc.perform(
                        put(BeerOrderController.BEER_ORDER_PATH_ID, beerOrder.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerOrderUpdateDTO))
                                .with(jwtRequestPostProcessor)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerRef", is("TestRef")));
    }

    @Test
    void test_delete_beer_order() throws Exception {
        var beerOrder = beerOrderRepository.findAll().getFirst();

        mockMvc.perform(
                        delete(BeerOrderController.BEER_ORDER_PATH_ID, beerOrder.getId())
                                .with(jwtRequestPostProcessor)
                )
                .andExpect(status().isNoContent());

        assertTrue(beerOrderRepository.findById(beerOrder.getId()).isEmpty());

        // Verify that the order is deleted
        mockMvc.perform(
                        get(BeerOrderController.BEER_ORDER_PATH_ID, beerOrder.getId())
                                .with(jwtRequestPostProcessor)
                )
                .andExpect(status().isNotFound());
    }

}