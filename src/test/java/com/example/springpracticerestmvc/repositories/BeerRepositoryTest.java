package com.example.springpracticerestmvc.repositories;

import com.example.springpracticerestmvc.entities.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void test_save_beer() {
        var beer = new Beer();
        beer.setBeerName("My Beer");

        var savedbeer = beerRepository.save(beer);

        assertThat(savedbeer).isNotNull();
        assertThat(savedbeer.getId()).isNotNull();
    }

}