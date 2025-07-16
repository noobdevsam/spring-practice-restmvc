package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.config.SecConfig;
import com.example.springpracticerestmvc.model.CustomerDTO;
import com.example.springpracticerestmvc.services.CustomerService;
import com.example.springpracticerestmvc.services.impl.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the CustomerController class.
 * This class tests the CustomerController endpoints using MockMvc and Mockito.
 */
@WebMvcTest(CustomerController.class)
@Import(SecConfig.class)
public class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc; // MockMvc instance for testing HTTP requests.

    @MockitoBean
    CustomerService customerService; // Mocked CustomerService for simulating service layer behavior.

    @Autowired
    ObjectMapper objectMapper; // ObjectMapper for JSON serialization/deserialization.

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor; // Captor for capturing UUID arguments in method calls.

    @Captor
    ArgumentCaptor<CustomerDTO> customerArgumentCaptor; // Captor for capturing CustomerDTO arguments in method calls.

    CustomerServiceImpl customerServiceImpl; // Implementation of CustomerService for testing purposes.

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }

    /**
     * Tests the endpoint for retrieving a customer by its ID.
     * Verifies that the response contains the correct customer details.
     *
     * @throws Exception if the request fails.
     */
    @Test
    void test_get_by_id() throws Exception {
        var customer = customerServiceImpl.getAllCustomers().getFirst();

        given(customerService.getCustomerById(customer.getId())).willReturn(
                Optional.of(customer)
        );

        mockMvc.perform(
                        get(CustomerController.CUSTOMER_PATH_ID, customer.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(customer.getName())));
    }

    /**
     * Tests the endpoint for listing all customers.
     * Verifies that the response contains the expected number of customers.
     *
     * @throws Exception if the request fails.
     */
    @Test
    void test_list_all_customers() throws Exception {
        given(customerService.getAllCustomers()).willReturn(customerServiceImpl.getAllCustomers());

        mockMvc.perform(
                        get(CustomerController.CUSTOMER_PATH)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));
    }

    /**
     * Tests the endpoint for creating a new customer.
     * Verifies that the response status is 201 Created and the Location header is present.
     *
     * @throws Exception if the request fails.
     */
    @Test
    void test_create_new_customer() throws Exception {
        var customer = customerServiceImpl.getAllCustomers().getFirst();
        customer.setId(UUID.randomUUID());
        customer.setVersion(null);

        given(customerService.saveNewCustomer(any(CustomerDTO.class)))
                .willReturn(customerServiceImpl.getAllCustomers().getFirst());

        mockMvc.perform(
                        post(CustomerController.CUSTOMER_PATH)
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer))
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    /**
     * Tests the endpoint for updating an existing customer.
     * Verifies that the response status is 204 No Content and the customer details are updated.
     *
     * @throws Exception if the request fails.
     */
    @Test
    void test_update_customer() throws Exception {
        var customer = customerServiceImpl.getAllCustomers().getFirst();

        given(
                customerService.updateCustomerById(
                        any(), any()
                )
        ).willReturn(Optional.of(
                new CustomerDTO()
        ));

        mockMvc.perform(
                put(CustomerController.CUSTOMER_PATH_ID, customer.getId())
                        .with(BeerControllerTest.jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
        ).andExpect(status().isNoContent());

        // Verifies certain behavior (happened once).
        verify(customerService).updateCustomerById(uuidArgumentCaptor.capture(), any(CustomerDTO.class));

        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }

    /**
     * Tests the endpoint for deleting a customer by its ID.
     * Verifies that the response status is 204 No Content and the customer is deleted.
     *
     * @throws Exception if the request fails.
     */
    @Test
    void test_delete_customer() throws Exception {
        var customer = customerServiceImpl.getAllCustomers().getFirst();

        given(customerService.deleteCustomerById(any())).willReturn(true);

        mockMvc.perform(
                delete(CustomerController.CUSTOMER_PATH_ID, customer.getId())
                        .with(BeerControllerTest.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());

        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }

    /**
     * Tests the endpoint for partially updating a customer.
     * Verifies that the response status is 204 No Content and the customer details are updated.
     *
     * @throws Exception if the request fails.
     */
    @Test
    void test_patch_customer() throws Exception {
        var customer = customerServiceImpl.getAllCustomers().getFirst();

        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("name", "New Name");

        mockMvc.perform(
                patch(CustomerController.CUSTOMER_PATH_ID, customer.getId())
                        .with(BeerControllerTest.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerMap))
        ).andExpect(status().isNoContent());

        verify(customerService).patchCustomerById(
                uuidArgumentCaptor.capture(),
                customerArgumentCaptor.capture()
        );

        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(customerMap.get("name")).isEqualTo(customerArgumentCaptor.getValue().getName());
    }

    /**
     * Tests the behavior when attempting to retrieve a customer by a non-existent ID.
     * Verifies that the response status is 404 Not Found.
     *
     * @throws Exception if the request fails.
     */
    @Test
    void test_getcustomerbyid_not_found() throws Exception {

        given(customerService.getCustomerById(any(UUID.class)))
                .willReturn(Optional.empty());

        mockMvc.perform(
                get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID())
                        .with(BeerControllerTest.jwtRequestPostProcessor)
        ).andExpect(status().isNotFound());
    }
}