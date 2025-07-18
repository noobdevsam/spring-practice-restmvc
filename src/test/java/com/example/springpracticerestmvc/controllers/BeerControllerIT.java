package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.config.SecConfig;
import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.events.BeerCreatedEvent;
import com.example.springpracticerestmvc.events.BeerDeletedEvent;
import com.example.springpracticerestmvc.events.BeerPatchedEvent;
import com.example.springpracticerestmvc.events.BeerUpdatedEvent;
import com.example.springpracticerestmvc.exceptions.NotFoundException;
import com.example.springpracticerestmvc.mappers.BeerMapper;
import com.example.springpracticerestmvc.model.BeerDTO;
import com.example.springpracticerestmvc.model.BeerStyle;
import com.example.springpracticerestmvc.repositories.BeerOrderRepository;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the BeerController class.
 * This class tests various endpoints and functionalities of the BeerController.
 */
@SpringBootTest
@ActiveProfiles("localdb")
@Import(SecConfig.class)
@RecordApplicationEvents
class BeerControllerIT {

    @Autowired
    ApplicationEvents applicationEvents;

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    /**
     * Sets up the MockMvc instance with Spring Security before each test.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    /**
     * Tests listing beers with default parameters.
     * Expects a large number of beers to be returned.
     */
    @Test
    void test_list_beers() {
        var dtos = beerController.listBeers(null, null, false, 1, 2412);

        assertThat(dtos.getContent().size()).isEqualTo(1000);
    }

    /**
     * Tests listing beers when the database is empty.
     * Expects an empty list to be returned.
     */
    @Test
    @Transactional
    @Rollback
    void test_empty_list() {
        beerOrderRepository.deleteAll();
        beerRepository.deleteAll();
        var dtos = beerController.listBeers(null, null, false, 1, 25);

        assertThat(dtos.getContent().size()).isEqualTo(0);
    }

    /**
     * Tests listing beers filtered by name using MockMvc.
     * Expects a specific number of beers to be returned.
     */
    @Test
    void test_list_beers_by_name() throws Exception {
        mockMvc.perform(
                        get(BeerController.BEER_PATH)
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .queryParam("beerName", "IPA")
                                .queryParam("pageSize", "800")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(320)));
    }

    /**
     * Tests listing beers filtered by style using MockMvc.
     * Expects a specific number of beers to be returned.
     */
    @Test
    void test_list_beers_by_style() throws Exception {
        mockMvc.perform(
                        get(BeerController.BEER_PATH)
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("pageSize", "800")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(556)));
    }

    /**
     * Tests listing beers filtered by both name and style using MockMvc.
     * Expects a specific number of beers to be returned.
     */
    @Test
    void test_list_beers_by_name_and_style() throws Exception {
        mockMvc.perform(
                        get(BeerController.BEER_PATH)
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .queryParam("beerName", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("pageSize", "800")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(298)));
    }

    /**
     * Tests listing beers filtered by name and style with inventory hidden.
     * Expects inventory data to be null.
     */
    @Test
    void test_list_beers_by_name_and_style_show_inventory_false() throws Exception {
        mockMvc.perform(
                        get(BeerController.BEER_PATH)
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .queryParam("beerName", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("showInventory", "false")
                                .queryParam("pageSize", "800")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(298)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.nullValue()));
    }

    /**
     * Tests listing beers filtered by name and style with inventory shown.
     * Expects inventory data to be present.
     */
    @Test
    void test_list_beers_by_name_and_style_show_inventory_true() throws Exception {
        mockMvc.perform(
                        get(BeerController.BEER_PATH)
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .queryParam("beerName", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("showInventory", "true")
                                .queryParam("pageSize", "800")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(298)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    /**
     * Tests listing beers filtered by name and style with pagination.
     * Expects a specific number of beers on the second page.
     */
    @Test
    void test_list_beers_by_name_and_style_show_inventory_true_page_2() throws Exception {
        mockMvc.perform(
                        get(BeerController.BEER_PATH)
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .queryParam("beerName", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("showInventory", "true")
                                .queryParam("pageNumber", "2")
                                .queryParam("pageSize", "50")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(50)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    /**
     * Tests fetching a beer by ID when the beer does not exist.
     * Expects a NotFoundException to be thrown.
     */
    @Test
    void test_beer_not_found() {
        assertThrows(NotFoundException.class, () -> beerController.getBeerById(UUID.randomUUID()));
    }

    /**
     * Tests fetching a beer by ID when the beer exists.
     * Expects the beer to be returned successfully.
     */
    @Test
    void test_get_by_id() {
        var beer = beerRepository.findAll().getFirst();
        var dto = beerController.getBeerById(beer.getId());

        assertThat(dto).isNotNull();
    }

    /**
     * Tests saving a new beer using the controller.
     * Expects the beer to be saved and a 201 status code returned.
     */
    @Test
    @Transactional
    @Rollback
    void test_save_new_beer() {
        var beerDto = new BeerDTO();
        beerDto.setBeerName("New Beer");

        var responseEntity = beerController.handlePost(beerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        var savedUUID = UUID.fromString(locationUUID[4]);
        var beer = beerRepository.findById(savedUUID).get();

        assertThat(beer).isNotNull();
    }

    /**
     * Tests updating an existing beer using the controller.
     * Expects the beer to be updated successfully.
     */
    @Test
    void test_update_beer() {
        var beer = beerRepository.findAll().getFirst();
        var beerDTO = beerMapper.beerToBeerDto(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(beer.getVersion());
        final String beerName = "updated!";
        beerDTO.setBeerName(beerName);

        var responseEntity = beerController.updateBeerById(beer.getId(), beerDTO);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        var updatedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(updatedBeer.getBeerName()).isEqualTo(beerName);
    }

    /**
     * Tests updating a beer by ID when the beer does not exist.
     * Expects a NotFoundException to be thrown.
     */
    @Test
    void test_update_not_found() {
        assertThrows(NotFoundException.class, () -> beerController.updateBeerById(UUID.randomUUID(), new BeerDTO()));
    }

    /**
     * Tests deleting a beer by ID when the beer exists.
     * Expects the beer to be deleted successfully.
     */
    @Test
    void test_delete_by_id_found() {
        var beer = beerRepository.save(
                Beer.builder()
                        .beerName("Beer to delete")
                        .beerStyle(BeerStyle.IPA)
                        .upc("123456789012")
                        .price(new BigDecimal("9.99"))
                        .build()
        );
        var responseEntity = beerController.deleteById(beer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(beerRepository.findById(beer.getId())).isEmpty();
    }

    /**
     * Tests deleting a beer by ID when the beer does not exist.
     * Expects a NotFoundException to be thrown.
     */
    @Test
    void test_delete_by_id_not_found() {
        assertThrows(NotFoundException.class, () -> beerController.deleteById(UUID.randomUUID()));
    }

    /**
     * Tests patching a beer with an invalid name using MockMvc.
     * Expects a 400 Bad Request status code.
     */
    @Test
    void test_patch_beer_bad_name() throws Exception {
        var beer = beerRepository.findAll().getFirst();

        Map<String, Object> beer_map = new HashMap<>();
        beer_map.put("beerName", "New Name slkdjfslkjfoieujwoiekdslmcxmvnerkjsdfjslkj");

        MvcResult result = mockMvc.perform(
                        patch(BeerController.BEER_PATH_ID, beer.getId())
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beer_map))

                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    /**
     * Tests updating a beer with a bad version to simulate transaction lock.
     * This test is disabled by default.
     */
    @Disabled
    @Test
    void test_update_beer_bad_version() throws Exception {
        var beer = beerRepository.findAll().getFirst();
        var beerDTO = beerMapper.beerToBeerDto(beer);

        beerDTO.setBeerName("Updated name");

        var result = mockMvc.perform(
                        put(BeerController.BEER_PATH_ID, beer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDTO)
                                )
                )
                .andExpect(status().isNoContent())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

        // Simulate a concurrent update with a different version
        beerDTO.setBeerName("Updated name again");

        var result_second = mockMvc.perform(
                        put(BeerController.BEER_PATH_ID, beer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDTO)
                                )
                )
                .andExpect(status().isNoContent())
                .andReturn();

        System.out.println(result_second.getResponse().getStatus());
    }

    /**
     * Tests creating a new beer using MockMvc.
     * Expects the beer to be created and a BeerCreatedEvent to be published.
     */
    @Test
    void test_created_beer_mvc() throws Exception {
        var beerDTO = new BeerDTO();
        beerDTO.setBeerName("New beer name");
        beerDTO.setBeerStyle(BeerStyle.IPA);
        beerDTO.setUpc("13213");
        beerDTO.setPrice(new BigDecimal("12.99"));
        beerDTO.setQuantityOnHand(100);

        mockMvc.perform(
                        post(BeerController.BEER_PATH)
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDTO))
                )
                .andExpect(status().isCreated())
                .andReturn();

        Assertions.assertEquals(
                1, applicationEvents.stream(BeerCreatedEvent.class).count()
        );
    }

    /**
     * Tests updating a beer using MockMvc.
     * Expects the beer to be updated and a BeerUpdatedEvent to be published.
     */
    @Test
    void test_update_beer_mvc() throws Exception {
        var beer = beerRepository.findAll().getFirst();
        var beerDTO = beerMapper.beerToBeerDto(beer);

        beerDTO.setBeerName("Updated beer name");

        mockMvc.perform(
                        put(BeerController.BEER_PATH_ID, beer.getId())
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDTO))
                )
                .andExpect(status().isNoContent())
                .andReturn();

        Assertions.assertEquals(
                1, applicationEvents.stream(BeerUpdatedEvent.class).count()
        );
    }

    /**
     * Tests patching a beer using MockMvc.
     * Expects the beer to be patched and a BeerPatchedEvent to be published.
     */
    @Test
    void test_patch_beer_mvc() throws Exception {
        var beer = beerRepository.findAll().getFirst();

        Map<String, Object> beer_map = new HashMap<>();
        beer_map.put("beerName", "New Name");

        mockMvc.perform(
                        patch(BeerController.BEER_PATH_ID, beer.getId())
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beer_map))
                )
                .andExpect(status().isNoContent())
                .andReturn();

        Assertions.assertEquals(
                1, applicationEvents.stream(BeerPatchedEvent.class).count()
        );
    }

    /**
     * Tests deleting a beer by ID using MockMvc.
     * Expects the beer to be deleted and a BeerDeletedEvent to be published.
     */
    @Test
    void test_delete_beer_id_found() throws Exception {
        beerOrderRepository.deleteAll();
        var beer = beerRepository.findAll().getFirst();

        mockMvc.perform(
                        delete(BeerController.BEER_PATH_ID, beer.getId())
                                .with(BeerControllerTest.jwtRequestPostProcessor)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

        Assertions.assertEquals(
                1, applicationEvents.stream(BeerDeletedEvent.class).count()
        );
    }

}