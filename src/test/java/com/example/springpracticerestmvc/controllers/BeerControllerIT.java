package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.exceptions.NotFoundException;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BeerControllerIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Test
    void test_list_beers() {
        var dtos = beerController.listBeers();

        assertThat(dtos.size()).isEqualTo(3);
    }

    @Test
    @Transactional
    @Rollback
    void test_empty_list() {
        beerRepository.deleteAll();
        var dtos = beerController.listBeers();

        assertThat(dtos.size()).isEqualTo(0);
    }

    @Test
    void test_beer_not_found() {
        assertThrows(NotFoundException.class, () -> {
            beerController.getBeerById(UUID.randomUUID());
        });
    }

    @Test
    void test_get_by_id() {
        var beer = beerRepository.findAll().getFirst();
        var dto = beerController.getBeerById(beer.getId());

        assertThat(dto).isNotNull();
    }
}