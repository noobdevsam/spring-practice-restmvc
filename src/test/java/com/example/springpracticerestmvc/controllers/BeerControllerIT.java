package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.exceptions.NotFoundException;
import com.example.springpracticerestmvc.mappers.BeerMapper;
import com.example.springpracticerestmvc.model.BeerDTO;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.core.Is.is;

@SpringBootTest
@ActiveProfiles("jpa")
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
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void test_list_beers() {
        var dtos = beerController.listBeers();

        assertThat(dtos.size()).isEqualTo(2);
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
        beerDTO.setVersion(null);
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
    void test_patch_beer_bad_name() throws Exception{
        var beer = beerRepository.findAll().getFirst();

        Map<String, Object> beer_map = new HashMap<>();
        beer_map.put("beerName", "New Name slkdjfslkjfoieujwoiekdslmcxmvnerkjsdfjslkj");

        MvcResult result = mockMvc.perform(
                    patch(BeerController.BEER_PATH_ID, beer.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(beer_map))

                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }
}















