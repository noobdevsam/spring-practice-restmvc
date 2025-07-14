package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.exceptions.NotFoundException;
import com.example.springpracticerestmvc.model.BeerOrderCreateDTO;
import com.example.springpracticerestmvc.model.BeerOrderDTO;
import com.example.springpracticerestmvc.model.BeerOrderUpdateDTO;
import com.example.springpracticerestmvc.services.BeerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Controller for managing Beer Order-related operations.
 * Provides endpoints for CRUD operations on Beer Order entities.
 */
@RestController
@RequiredArgsConstructor
public class BeerOrderController {

    /**
     * Base path for Beer Order API endpoints.
     */
    public static final String BEER_ORDER_PATH = "/api/v1/beer-order";

    /**
     * Path for Beer Order API endpoints with Beer Order ID.
     */
    public static final String BEER_ORDER_PATH_ID = BEER_ORDER_PATH + "/{beerOrderId}";

    /**
     * Service for Beer Order-related business logic.
     */
    private final BeerOrderService beerOrderService;

    /**
     * Endpoint to retrieve a beer order by its ID.
     *
     * @param beerOrderId UUID of the beer order to retrieve.
     * @return The BeerOrderDTO object for the specified beer order ID.
     * @throws NotFoundException if the beer order is not found.
     */
    @GetMapping(BEER_ORDER_PATH_ID)
    public BeerOrderDTO getBeerOrderById(@PathVariable UUID beerOrderId) {
        return beerOrderService
                .getById(beerOrderId)
                .orElseThrow(NotFoundException::new);
    }

    /**
     * Endpoint to list beer orders with optional pagination.
     *
     * @param pageNumber Optional page number for pagination.
     * @param pageSize   Optional page size for pagination.
     * @return A paginated list of BeerOrderDTO objects.
     */
    @GetMapping(BEER_ORDER_PATH)
    public Page<BeerOrderDTO> listBeerOrders(
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        return beerOrderService
                .listOrders(pageNumber, pageSize);
    }

    /**
     * Endpoint to create a new beer order.
     *
     * @param beerOrderCreateDTO The BeerOrderCreateDTO object containing beer order details.
     * @return ResponseEntity with the location of the created beer order.
     */
    @PostMapping(BEER_ORDER_PATH)
    public ResponseEntity<Void> createOrder(
            @RequestBody BeerOrderCreateDTO beerOrderCreateDTO
    ) {
        var savedOrder = beerOrderService.createOrder(beerOrderCreateDTO);

        return ResponseEntity.created(
                URI.create(BEER_ORDER_PATH + "/" + savedOrder.getId().toString())
        ).build();
    }

    /**
     * Endpoint to update an existing beer order by its ID.
     *
     * @param beerOrderId        UUID of the beer order to update.
     * @param beerOrderUpdateDTO The BeerOrderUpdateDTO object containing updated beer order details.
     * @return ResponseEntity with the updated BeerOrderDTO object.
     */
    @PutMapping(BEER_ORDER_PATH_ID)
    public ResponseEntity<BeerOrderDTO> updateOrder(
            @PathVariable UUID beerOrderId,
            @RequestBody BeerOrderUpdateDTO beerOrderUpdateDTO
    ) {
        return ResponseEntity.ok(
                beerOrderService.updateOrder(beerOrderId, beerOrderUpdateDTO)
        );
    }

    /**
     * Endpoint to delete a beer order by its ID.
     *
     * @param beerOrderId UUID of the beer order to delete.
     * @return ResponseEntity with HTTP status NO_CONTENT.
     */
    @DeleteMapping(BEER_ORDER_PATH_ID)
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID beerOrderId) {
        beerOrderService.deleteOrder(beerOrderId);
        return ResponseEntity.noContent().build();
    }

}