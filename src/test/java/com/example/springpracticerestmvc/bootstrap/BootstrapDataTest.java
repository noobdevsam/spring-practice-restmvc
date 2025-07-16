package com.example.springpracticerestmvc.bootstrap;

import com.example.springpracticerestmvc.repositories.BeerOrderRepository;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.example.springpracticerestmvc.repositories.CustomerRepository;
import com.example.springpracticerestmvc.services.BeerCsvService;
import com.example.springpracticerestmvc.services.impl.BeerCsvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test class for the BootstrapData component.
 * This class tests the functionality of the BootstrapData class
 * which initializes data for the application.
 */
@DataJpaTest
@Import(BeerCsvServiceImpl.class)
class BootstrapDataTest {

    // Repository for managing Beer entities
    @Autowired
    BeerRepository beerRepository;

    // Repository for managing Customer entities
    @Autowired
    CustomerRepository customerRepository;

    // Repository for managing BeerOrder entities
    @Autowired
    BeerOrderRepository beerOrderRepository;

    // Service for handling CSV operations related to Beer entities
    @Autowired
    BeerCsvService csvService;

    // Instance of BootstrapData to be tested
    BootstrapData bootstrapData;

    /**
     * Sets up the test environment before each test method.
     * Initializes the BootstrapData instance with required dependencies.
     */
    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(beerRepository, customerRepository, beerOrderRepository, csvService);
    }

    /**
     * Tests the run method of BootstrapData.
     * Verifies that the expected number of Beer and Customer entities are loaded.
     *
     * @throws Exception if an error occurs during the execution of the run method
     */
    @Test
    void test_run() throws Exception {
        bootstrapData.run(null); // Executes the data initialization logic
        assertThat(beerRepository.count()).isEqualTo(2413); // Verifies Beer entity count
        assertThat(customerRepository.count()).isEqualTo(3); // Verifies Customer entity count
    }
}