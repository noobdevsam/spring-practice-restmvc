package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.exceptions.NotFoundException;
import com.example.springpracticerestmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void test_list_customers() {
        var dtos = customerController.listAllCustomers();

        assertThat(dtos.size()).isEqualTo(3);
    }

    @Test
    void test_get_customer_by_id() {
        var customer = customerRepository.findAll().getFirst();
        var customerDTO = customerController.getCustomerById(customer.getId());
        assertThat(customerDTO).isNotNull();
    }

    @Test
    void test_customer_not_found_by_id() {
        assertThrows(NotFoundException.class, () -> {
            customerController.getCustomerById(UUID.randomUUID());
        });
    }

    @Test
    @Transactional
    @Rollback
    void test_finds_empty_list() {
        customerRepository.deleteAll();
        var dtos = customerController.listAllCustomers();

        assertThat(dtos.size()).isEqualTo(0);
    }
}