package com.example.springpracticerestmvc;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.entities.Customer;
import com.example.springpracticerestmvc.model.BeerCSVRecord;
import com.example.springpracticerestmvc.model.BeerStyle;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.example.springpracticerestmvc.repositories.CustomerRepository;
import com.example.springpracticerestmvc.services.BeerCsvService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class Application {

	private final BeerRepository beerRepository;
	private final CustomerRepository customerRepository;
	private final BeerCsvService beerCsvService;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Transactional
	@Bean
	CommandLineRunner bootstrap() throws Exception {
		return args -> {
			loadBeerData();
			loadCsvData();
			loadCustomerData();
		};
	}

	private void loadCsvData() throws FileNotFoundException {

		if (beerRepository.count() < 10) {
			File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
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
