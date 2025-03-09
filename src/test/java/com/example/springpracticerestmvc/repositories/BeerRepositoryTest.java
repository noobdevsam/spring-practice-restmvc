package com.example.springpracticerestmvc.repositories;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void test_save_beer() {
        var beer = new Beer();
        beer.setBeerName("My Beer");
        beer.setBeerStyle(BeerStyle.PALE_ALE);
        beer.setUpc("78654351");
        beer.setPrice(new BigDecimal("11.94"));

        var savedbeer = beerRepository.save(beer);
        beerRepository.flush();

        assertThat(savedbeer).isNotNull();
        assertThat(savedbeer.getId()).isNotNull();
    }

}