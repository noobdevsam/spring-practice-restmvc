package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.model.Beer;
import com.example.springpracticerestmvc.services.BeerService;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/beer")
@SuppressWarnings("rawtypes")
public class BeerController {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BeerController.class);
    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping
    public List<Beer> listBeers() {
        return beerService.listBeers();
    }

    @GetMapping("{beerId}")
    public Beer getBeerById(@PathVariable("beerId") UUID beerId) {
        log.debug("get beer by id - in controller");
        log.debug("requested beer id: " + beerId);
        return beerService.getBeerById(beerId);
    }

    @SuppressWarnings("unchecked")
    @PostMapping
    public ResponseEntity handlePost(@RequestBody Beer beer) {
        var savedBeer = beerService.saveNewBeer(beer);

        var headers = new HttpHeaders();
        headers.add("Location", "/api/v1/beer/" + savedBeer.getId().toString());

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{beerId}")
    public ResponseEntity updateBeerById(@PathVariable("beerId") UUID beerId, @RequestBody Beer beer) {
        beerService.updateBeerById(beerId, beer);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{beerId}")
    public ResponseEntity deleteById(@PathVariable("beerId") UUID beerId) {
        beerService.deleteById(beerId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{beerId}")
    public ResponseEntity patchBeerById(@PathVariable("beerId") UUID beerId, @RequestBody Beer beer) {
        beerService.patchBeerById(beerId, beer);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
