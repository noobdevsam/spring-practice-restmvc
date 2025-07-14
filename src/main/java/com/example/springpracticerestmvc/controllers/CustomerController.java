package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.exceptions.NotFoundException;
import com.example.springpracticerestmvc.model.CustomerDTO;
import com.example.springpracticerestmvc.services.CustomerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing Customer-related operations.
 * Provides endpoints for CRUD operations on Customer entities.
 */
@RestController
@SuppressWarnings("rawtypes")
public class CustomerController {

    /**
     * Service for Customer-related business logic.
     */
    private final CustomerService customerService;

    /**
     * Base path for Customer API endpoints.
     */
    public final static String CUSTOMER_PATH = "/api/v1/customer";

    /**
     * Path for Customer API endpoints with Customer ID.
     */
    public final static String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{customerId}";

    /**
     * Constructor for CustomerController.
     *
     * @param customerService Service for Customer-related operations.
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Endpoint to list all customers.
     *
     * @return A list of CustomerDTO objects.
     */
    @GetMapping(CUSTOMER_PATH)
    public List<CustomerDTO> listAllCustomers() {
        return customerService.getAllCustomers();
    }

    /**
     * Endpoint to retrieve a customer by its ID.
     *
     * @param id UUID of the customer to retrieve.
     * @return The CustomerDTO object for the specified customer ID.
     * @throws NotFoundException if the customer is not found.
     */
    @GetMapping(CUSTOMER_PATH_ID)
    public CustomerDTO getCustomerById(@PathVariable("customerId") UUID id) {
        return customerService.getCustomerById(id)
                .orElseThrow(NotFoundException::new);
    }

    /**
     * Endpoint to create a new customer.
     *
     * @param customerDTO The CustomerDTO object containing customer details.
     * @return ResponseEntity with the location of the created customer.
     */
    @SuppressWarnings("unchecked")
    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity handlePost(@RequestBody CustomerDTO customerDTO) {
        CustomerDTO savedCustomerDTO = customerService.saveNewCustomer(customerDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", CUSTOMER_PATH + "/" + savedCustomerDTO.getId().toString());

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    /**
     * Endpoint to update an existing customer by its ID.
     *
     * @param customerId  UUID of the customer to update.
     * @param customerDTO The CustomerDTO object containing updated customer details.
     * @return ResponseEntity with HTTP status NO_CONTENT.
     * @throws NotFoundException if the customer is not found.
     */
    @PutMapping(CUSTOMER_PATH_ID)
    public ResponseEntity updateCustomerById(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customerDTO) {
        if (customerService.updateCustomerById(customerId, customerDTO).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint to delete a customer by its ID.
     *
     * @param customerId UUID of the customer to delete.
     * @return ResponseEntity with HTTP status NO_CONTENT.
     * @throws NotFoundException if the customer is not found.
     */
    @DeleteMapping(CUSTOMER_PATH_ID)
    public ResponseEntity deleteCustomerId(@PathVariable("customerId") UUID customerId) {
        if (!customerService.deleteCustomerById(customerId)) {
            throw new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint to partially update a customer by its ID.
     *
     * @param customerId  UUID of the customer to patch.
     * @param customerDTO The CustomerDTO object containing partial updates.
     * @return ResponseEntity with HTTP status NO_CONTENT.
     */
    @PatchMapping(CUSTOMER_PATH_ID)
    public ResponseEntity patchCustomerById(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customerDTO) {
        customerService.patchCustomerById(customerId, customerDTO);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}