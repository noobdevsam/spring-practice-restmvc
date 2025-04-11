package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.model.BeerCSVRecord;
import com.example.springpracticerestmvc.services.BeerCsvService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
@Profile({"localdb", "jpa", "default"})
@Primary
public class BeerCsvServiceImpl implements BeerCsvService {

    @Override
    public List<BeerCSVRecord> convertCSV(File csvFile) {

        try {
            return new CsvToBeanBuilder<BeerCSVRecord>(
                    new FileReader(csvFile)
            )
                    .withType(BeerCSVRecord.class)
                    .build()
                    .parse();
        } catch (FileNotFoundException exception) {
            throw new RuntimeException(exception);
        }

    }

}
