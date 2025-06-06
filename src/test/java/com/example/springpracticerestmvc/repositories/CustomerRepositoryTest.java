package com.example.springpracticerestmvc.repositories;

import com.example.springpracticerestmvc.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void test_customer_save() {
        var customer = new Customer();
        customer.setName("My Customer");

        var savedcustomer = customerRepository.save(customer);

        assertThat(savedcustomer.getId()).isNotNull();
    }

}