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

/**
 * Implementation of the BeerCsvService interface for converting CSV files into BeerCSVRecord objects.
 * This service is marked as the primary implementation and is active under the "localdb" and "default" profiles.
 */
@Service
@Profile({"localdb", "default"})
@Primary
public class BeerCsvServiceImpl implements BeerCsvService {

    /**
     * Converts a given CSV file into a list of BeerCSVRecord objects.
     *
     * @param csvFile The CSV file to be converted.
     * @return A list of BeerCSVRecord objects parsed from the CSV file.
     * @throws RuntimeException If the file is not found or cannot be read.
     */
    @Override
    public List<BeerCSVRecord> convertCSV(File csvFile) {
        try {
            // Use CsvToBeanBuilder to parse the CSV file into BeerCSVRecord objects
            return new CsvToBeanBuilder<BeerCSVRecord>(
                    new FileReader(csvFile)
            )
                    .withType(BeerCSVRecord.class) // Specify the type of objects to be created
                    .build() // Build the CsvToBean instance
                    .parse(); // Parse the CSV file
        } catch (FileNotFoundException exception) {
            // Throw a RuntimeException if the file is not found
            throw new RuntimeException(exception);
        }
    }
}