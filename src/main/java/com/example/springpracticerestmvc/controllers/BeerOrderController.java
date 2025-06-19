package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.exceptions.NotFoundException;
import com.example.springpracticerestmvc.model.BeerOrderCreateDTO;
import com.example.springpracticerestmvc.model.BeerOrderDTO;
import com.example.springpracticerestmvc.services.BeerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BeerOrderController {

    public static final String BEER_ORDER_PATH = "/api/v1/beer-order";
    public static final String BEER_ORDER_PATH_ID = BEER_ORDER_PATH + "/{beerOrderId}";

    private final BeerOrderService beerOrderService;

    @GetMapping(BEER_ORDER_PATH_ID)
    public BeerOrderDTO getBeerOrderById(@PathVariable UUID beerOrderId) {
        return beerOrderService
                .getById(beerOrderId)
                .orElseThrow(NotFoundException::new);
    }

    @GetMapping(BEER_ORDER_PATH)
    public Page<BeerOrderDTO> listBeerOrders(
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        return beerOrderService
                .listOrders(pageNumber, pageSize);
    }

    @PostMapping(BEER_ORDER_PATH)
    public ResponseEntity<Void> createOrder(
            @RequestBody BeerOrderCreateDTO beerOrderCreateDTO
    ) {

        var savedOrder = beerOrderService.createOrder(beerOrderCreateDTO);

        return ResponseEntity.created(
                URI.create(BEER_ORDER_PATH + "/" + savedOrder.getId().toString())
        ).build();
    }

}
