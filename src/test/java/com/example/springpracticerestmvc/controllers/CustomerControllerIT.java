package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.bootstrap.BootstrapData;
import com.example.springpracticerestmvc.config.SecConfig;
import com.example.springpracticerestmvc.exceptions.NotFoundException;
import com.example.springpracticerestmvc.mappers.CustomerMapper;
import com.example.springpracticerestmvc.model.CustomerDTO;
import com.example.springpracticerestmvc.repositories.BeerOrderRepository;
import com.example.springpracticerestmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests for the CustomerController.
 * This class tests the CustomerController endpoints and their interactions with the database.
 */
@SpringBootTest
@ActiveProfiles("localdb")
@Import({SecConfig.class, BootstrapData.class})
class CustomerControllerIT {

    @Autowired
    CustomerController customerController; // Controller for handling customer-related requests.

    @Autowired
    CustomerRepository customerRepository; // Repository for Customer entities.

    @Autowired
    BeerOrderRepository beerOrderRepository; // Repository for BeerOrder entities.

    @Autowired
    CustomerMapper customerMapper; // Mapper for converting between Customer and CustomerDTO.

    /**
     * Tests the endpoint for listing all customers.
     * Verifies that the response contains the expected number of customers.
     */
    @Test
    void test_list_customers() {
        var dtos = customerController.listAllCustomers();

        assertThat(dtos.size()).isEqualTo(2);
    }

    /**
     * Tests the endpoint for retrieving a customer by its ID.
     * Verifies that the response contains the correct customer details.
     */
    @Test
    void test_get_customer_by_id() {
        var customer = customerRepository.findAll().getFirst();
        var customerDTO = customerController.getCustomerById(customer.getId());
        assertThat(customerDTO).isNotNull();
    }

    /**
     * Tests the behavior when attempting to retrieve a customer by a non-existent ID.
     * Verifies that a NotFoundException is thrown.
     */
    @Test
    void test_customer_not_found_by_id() {
        assertThrows(NotFoundException.class, () -> {
            customerController.getCustomerById(UUID.randomUUID());
        });
    }

    /**
     * Tests the endpoint for listing customers when the database is empty.
     * Verifies that the response contains an empty list.
     */
    @Test
    @Transactional
    @Rollback
    void test_finds_empty_list() {
        beerOrderRepository.deleteAll();
        customerRepository.deleteAll();
        var dtos = customerController.listAllCustomers();

        assertThat(dtos.size()).isEqualTo(0);
    }

    /**
     * Tests the endpoint for saving a new customer.
     * Verifies that the response status is 201 Created and the Location header is present.
     */
    @Test
    @Transactional
    @Rollback
    void test_save_new() {
        var customerDto = new CustomerDTO();
        customerDto.setName("Test");

        var responseEntity = customerController.handlePost(customerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String[] locationUUID = responseEntity.getHeaders().getLocation()
                .getPath().split("/");
        var savedId = UUID.fromString(locationUUID[4]);
        var customer = customerRepository.findById(savedId).get();

        assertThat(customer).isNotNull();
    }

    /**
     * Tests the endpoint for updating an existing customer.
     * Verifies that the response status is 204 No Content and the customer details are updated.
     */
    @Test
    @Transactional
    @Rollback
    void test_update_existing_customer() {
        var customer = customerRepository.findAll().getFirst();
        var cusDto = customerMapper.customerToCustomerDto(customer);

        final String customerName = "Updated!";
        cusDto.setName(customerName);
        cusDto.setId(null);
        cusDto.setVersion(null);

        var responseEntity = customerController.updateCustomerById(customer.getId(), cusDto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        var updatedCustomer = customerRepository.findById(customer.getId()).get();
        assertThat(updatedCustomer.getName()).isEqualTo(customerName);
    }

    /**
     * Tests the behavior when attempting to update a customer by a non-existent ID.
     * Verifies that a NotFoundException is thrown.
     */
    @Test
    void test_update_not_found() {
        assertThrows(NotFoundException.class, () -> {
            customerController.updateCustomerById(UUID.randomUUID(), new CustomerDTO());
        });
    }

    /**
     * Tests the endpoint for deleting a customer by its ID.
     * Verifies that the response status is 204 No Content and the customer is deleted.
     */
    @Test
    void test_delete_by_id_found() {
        beerOrderRepository.deleteAll();
        var customer = customerRepository.findAll().getFirst();
        var responseEntity = customerController.deleteCustomerId(customer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(customerRepository.findById(customer.getId())).isEmpty();
    }

    /**
     * Tests the behavior when attempting to delete a customer by a non-existent ID.
     * Verifies that a NotFoundException is thrown.
     */
    @Test
    void test_delete_not_found() {
        assertThrows(NotFoundException.class, () -> {
            customerController.deleteCustomerId(UUID.randomUUID());
        });
    }
}