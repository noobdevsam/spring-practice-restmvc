package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.model.Customer;
import com.example.springpracticerestmvc.services.CustomerService;
import com.example.springpracticerestmvc.services.impl.CustomerServiceImpl;
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

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {
    
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CustomerService customerService;

    @Autowired
    ObjectMapper objectMapper;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;
    // Use it to capture argument values for further assertions.

    @Captor
    ArgumentCaptor<Customer> customerArgumentCaptor;

    CustomerServiceImpl customerServiceImpl;

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Test
    void test_get_by_id() throws Exception{
        var customer = customerServiceImpl.getAllCustomers().getFirst();

        given(customerService.getCustomerById(customer.getId())).willReturn(customer);

        mockMvc.perform(
            get("/api/v1/customer/" + customer.getId()).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name", is(customer.getName())));
    }

    @Test
    void test_list_all_customers() throws Exception{
        given(customerService.getAllCustomers()).willReturn(customerServiceImpl.getAllCustomers());

        mockMvc.perform(
            get("/api/v1/customer").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void test_create_new_customer() throws Exception {
        var customer = customerServiceImpl.getAllCustomers().getFirst();
        customer.setId(null);
        customer.setVersion(null);

        given(customerService.saveNewCustomer(any(Customer.class)))
                .willReturn(customerServiceImpl.getAllCustomers().get(1));

        mockMvc.perform(
                        post("/api/v1/customer")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer))
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void test_update_customer() throws Exception {
        var customer = customerServiceImpl.getAllCustomers().getFirst();

        mockMvc.perform(
                put("/api/v1/customer/" + customer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
        ).andExpect(status().isNoContent());

        // Verifies certain behavior (happened once).
        verify(customerService).updateCustomerById(uuidArgumentCaptor.capture(), any(Customer.class));

        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }

    @Test
    void test_delete_customer() throws Exception {
        var customer = customerServiceImpl.getAllCustomers().getFirst();

        mockMvc.perform(
                delete("/api/v1/customer/" + customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());

        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }

    @Test
    void test_patch_customer() throws Exception {
        var customer = customerServiceImpl.getAllCustomers().getFirst();

        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("name", "New Name");

        mockMvc.perform(
                patch("/api/v1/customer/" + customer.getId())
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
}