package com.example.springpracticerestmvc.repositories;

import com.example.springpracticerestmvc.bootstrap.BootstrapData;
import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.model.BeerStyle;
import com.example.springpracticerestmvc.services.impl.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
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

    @Test
    void test_save_beer_name_too_long() {
        assertThrows(ConstraintViolationException.class, () -> {
            var beer = new Beer();
            beer.setBeerName("My Beerlsdjflkalkfjslfjewioujewdskmnfkdskjfsewljelklrljwelsdfgsewdsfsl");
            beer.setBeerStyle(BeerStyle.PALE_ALE);
            beer.setUpc("78654351");
            beer.setPrice(new BigDecimal("11.94"));

            beerRepository.save(beer);
            beerRepository.flush();
        });
    }

    @Test
    void test_get_beer_list_by_name() {
        Page<Beer> list = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%", null);

        assertThat(list.getContent().size()).isEqualTo(321);
    }

}