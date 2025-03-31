package com.example.springpracticerestmvc.repositories;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BeerRepository beerRepository;

    Beer beer;

    @BeforeEach
    void setUp() {
        beer = beerRepository.findAll().getFirst();
    }

    @Transactional
    @Test
    void test_add_and_remove_category() {
        var saved_category = categoryRepository.save(
                Category.builder()
                        .description("Test Category")
                        .build()
        );

        beer.addCategory(saved_category);
        var saved_beer = beerRepository.save(beer);

        System.out.println(saved_beer.getBeerName());

        saved_beer.removeCategory(saved_category);
        var saved_beer_2 = beerRepository.save(saved_beer);

        assertThat(saved_beer_2.getCategories().size()).isEqualTo(0);
    }

}