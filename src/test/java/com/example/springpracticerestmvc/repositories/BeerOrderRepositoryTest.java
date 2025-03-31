package com.example.springpracticerestmvc.repositories;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.entities.BeerOrder;
import com.example.springpracticerestmvc.entities.BeerOrderShipment;
import com.example.springpracticerestmvc.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class BeerOrderRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    Customer customer;
    Beer beer;

    @BeforeEach
    void setUp() {
        customer = customerRepository.findAll().getFirst();
        beer = beerRepository.findAll().getFirst();
    }

    @Transactional
    @Test
    void test_beer_order() {

        var saved_beer_order = beerOrderRepository.save(
                BeerOrder.builder()
                        .customerRef("Test order")
                        .customer(customer)
                        .beerOrderShipment(
                                BeerOrderShipment.builder()
                                        .trackingNumber("123456789")
                                        .build()
                        )
                        .build()
        );

        // This will cause a lazy loading exception, we need to use @Transactional
        System.out.println(saved_beer_order.getCustomerRef());

    }
}