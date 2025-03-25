package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.model.BeerCSVRecord;
import com.example.springpracticerestmvc.services.BeerCsvService;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BeerCsvServiceImplTest {

    BeerCsvService beerCsvService = new BeerCsvServiceImpl();

    @Test
    void test_convert_cvs() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");

        List<BeerCSVRecord> records = beerCsvService.convertCSV(file);

        System.out.println(records.size());

        assertThat(records.size()).isGreaterThan(0);
    }
}