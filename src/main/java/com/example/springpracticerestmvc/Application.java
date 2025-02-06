package com.example.springpracticerestmvc;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.entities.Customer;
import com.example.springpracticerestmvc.model.BeerStyle;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.example.springpracticerestmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
@RequiredArgsConstructor
public class Application {

	private final BeerRepository beerRepository;
	private final CustomerRepository customerRepository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner bootstrap() throws Exception {
		return args -> {
			loadBeerData();
			loadCustomerData();
		};
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
