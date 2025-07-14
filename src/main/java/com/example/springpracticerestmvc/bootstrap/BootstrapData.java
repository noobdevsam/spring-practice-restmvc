package com.example.springpracticerestmvc.bootstrap;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.entities.BeerOrder;
import com.example.springpracticerestmvc.entities.BeerOrderLine;
import com.example.springpracticerestmvc.entities.Customer;
import com.example.springpracticerestmvc.model.BeerCSVRecord;
import com.example.springpracticerestmvc.model.BeerStyle;
import com.example.springpracticerestmvc.repositories.BeerOrderRepository;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.example.springpracticerestmvc.repositories.CustomerRepository;
import com.example.springpracticerestmvc.services.BeerCsvService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * BootstrapData is a Spring Boot component that initializes the database with sample data.
 * It implements CommandLineRunner to execute code at application startup.
 */
@Component
@RequiredArgsConstructor
@Profile({"localdb", "default"})
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerCsvService beerCsvService;

    @Value("${bootstrap.csv-file-path}")
    String beerCsvFilePath;

    /**
     * Executes the database initialization logic at application startup.
     *
     * @param args Command-line arguments passed to the application.
     * @throws Exception if an error occurs during initialization.
     */
    @Transactional
    @Override
    public void run(String... args) throws Exception {
        if (beerCsvFilePath == null) {
            beerCsvFilePath = "classpath:csvdata/beers.csv";
        }

        loadBeerData();
        loadCsvData(beerCsvFilePath);
        loadCustomerData();
        loadOrderData();
    }

    /**
     * Loads sample beer order data into the database.
     * Creates orders for each customer using available beers.
     */
    private void loadOrderData() {
        if (beerOrderRepository.count() == 0) {
            val customers = customerRepository.findAll();
            val beers = beerRepository.findAll();
            val beerIterator = beers.iterator();

            customers.forEach(customer -> {
                beerOrderRepository.save(
                        BeerOrder.builder()
                                .customer(customer)
                                .beerOrderLines(
                                        Set.of(
                                                BeerOrderLine.builder()
                                                        .beer(beerIterator.next())
                                                        .orderQuantity(1)
                                                        .build(),
                                                BeerOrderLine.builder()
                                                        .beer(beerIterator.next())
                                                        .orderQuantity(2)
                                                        .build()
                                        )
                                )
                                .build()
                );

                beerOrderRepository.save(
                        BeerOrder.builder()
                                .customer(customer)
                                .beerOrderLines(
                                        Set.of(
                                                BeerOrderLine.builder()
                                                        .beer(beerIterator.next())
                                                        .orderQuantity(3)
                                                        .build(),
                                                BeerOrderLine.builder()
                                                        .beer(beerIterator.next())
                                                        .orderQuantity(4)
                                                        .build()
                                        )
                                )
                                .build()
                );
            });

            beerOrderRepository.findAll(); // Trigger loading of orders into the context
        }
    }

    /**
     * Loads beer data from a CSV file into the database.
     *
     * @param beerCsvFilePath Path to the CSV file containing beer data.
     * @throws FileNotFoundException if the CSV file is not found.
     */
    private void loadCsvData(String beerCsvFilePath) throws FileNotFoundException {
        if (beerRepository.count() < 10) {
            File file = ResourceUtils.getFile(beerCsvFilePath);
            List<BeerCSVRecord> csvRecords = beerCsvService.convertCSV(file);

            csvRecords.forEach(beerCSVRecord -> {
                BeerStyle beerStyle = switch (beerCSVRecord.getStyle()) {
                    case "American Pale Lager", "English Pale Ale" -> BeerStyle.PALE_ALE;
                    case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                            BeerStyle.ALE;
                    case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
                    case "American Porter" -> BeerStyle.PORTER;
                    case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
                    case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
                    case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
                    default -> BeerStyle.PILSNER;
                };

                beerRepository.save(Beer.builder()
                        .beerName(StringUtils.abbreviate(
                                beerCSVRecord.getBeer(), 50
                        ))
                        .beerStyle(beerStyle)
                        .price(BigDecimal.TEN)
                        .upc(beerCSVRecord.getRow().toString())
                        .quantityOnHand(beerCSVRecord.getCount())
                        .build());
            });
        }
    }

    /**
     * Loads sample customer data into the database.
     * Creates three customers with predefined names and timestamps.
     */
    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            var customer1 = Customer.builder()
                    .name("Customer 1")
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Customer customer2 = Customer.builder()
                    .name("Customer 2")
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Customer customer3 = Customer.builder()
                    .name("Customer 3")
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));
        }
    }

    /**
     * Loads predefined beer data into the database.
     * Adds three beers with specific attributes.
     */
    private void loadBeerData() {
        if (beerRepository.count() == 0) {
            beerRepository.save(
                    Beer.builder()
                            .beerName("Galaxy Cat")
                            .beerStyle(BeerStyle.PALE_ALE)
                            .upc("12356")
                            .price(new BigDecimal("12.99"))
                            .quantityOnHand(122)
                            .createdDate(LocalDateTime.now())
                            .updateDate(LocalDateTime.now())
                            .build()
            );

            beerRepository.save(
                    Beer.builder()
                            .beerName("Crank")
                            .beerStyle(BeerStyle.PALE_ALE)
                            .upc("12356222")
                            .price(new BigDecimal("11.99"))
                            .quantityOnHand(392)
                            .createdDate(LocalDateTime.now())
                            .updateDate(LocalDateTime.now())
                            .build()
            );

            beerRepository.save(
                    Beer.builder()
                            .beerName("Sunshine City")
                            .beerStyle(BeerStyle.IPA)
                            .upc("12356")
                            .price(new BigDecimal("13.99"))
                            .quantityOnHand(144)
                            .createdDate(LocalDateTime.now())
                            .updateDate(LocalDateTime.now())
                            .build()
            );
        }
    }
}