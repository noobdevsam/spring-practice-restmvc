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

@SpringBootTest
@ActiveProfiles("localdb")
@Import({SecConfig.class, BootstrapData.class})
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerMapper customerMapper;

    @Test
    void test_list_customers() {
        var dtos = customerController.listAllCustomers();

        assertThat(dtos.size()).isEqualTo(2);
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
        beerOrderRepository.deleteAll();
        customerRepository.deleteAll();
        var dtos = customerController.listAllCustomers();

        assertThat(dtos.size()).isEqualTo(0);
    }

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

    @Test
    void test_update_not_found() {
        assertThrows(NotFoundException.class, () -> {
            customerController.updateCustomerById(UUID.randomUUID(), new CustomerDTO());
        });
    }

    @Test
    void test_delete_by_id_found() {
        beerOrderRepository.deleteAll();
        var customer = customerRepository.findAll().getFirst();
        var responseEntity = customerController.deleteCustomerId(customer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(customerRepository.findById(customer.getId())).isEmpty();
    }

    @Test
    void test_delete_not_found() {
        assertThrows(NotFoundException.class, () -> {
            customerController.deleteCustomerId(UUID.randomUUID());
        });
    }


}