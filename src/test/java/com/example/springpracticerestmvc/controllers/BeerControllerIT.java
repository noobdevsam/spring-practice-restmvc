package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.config.SecConfig;
import com.example.springpracticerestmvc.exceptions.NotFoundException;
import com.example.springpracticerestmvc.mappers.BeerMapper;
import com.example.springpracticerestmvc.model.BeerDTO;
import com.example.springpracticerestmvc.model.BeerStyle;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

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

@SpringBootTest
@ActiveProfiles("localdb")
@Import(SecConfig.class)
class BeerControllerIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void test_list_beers() {
        var dtos = beerController.listBeers(null, null, false, 1, 2412);

        assertThat(dtos.getContent().size()).isEqualTo(1000);
    }

    @Test
    @Transactional
    @Rollback
    void test_empty_list() {
        beerRepository.deleteAll();
        var dtos = beerController.listBeers(null, null, false, 1, 25);

        assertThat(dtos.getContent().size()).isEqualTo(0);
    }

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

    @Test
    void test_beer_not_found() {
        assertThrows(NotFoundException.class, () -> beerController.getBeerById(UUID.randomUUID()));
    }

    @Test
    void test_get_by_id() {
        var beer = beerRepository.findAll().getFirst();
        var dto = beerController.getBeerById(beer.getId());

        assertThat(dto).isNotNull();
    }

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

    @Test
    void test_update_not_found() {
        assertThrows(NotFoundException.class, () -> beerController.updateBeerById(UUID.randomUUID(), new BeerDTO()));
    }

    @Test
    void test_delete_by_id_found() {
        var beer = beerRepository.findAll().getFirst();
        var responseEntity = beerController.deleteById(beer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(beerRepository.findById(beer.getId())).isEmpty();
    }

    @Test
    void test_delete_by_id_not_found() {
        assertThrows(NotFoundException.class, () -> beerController.deleteById(UUID.randomUUID()));
    }

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

    // transaction lock demo
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
}















