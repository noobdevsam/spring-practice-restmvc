package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.model.CustomerDTO;
import com.example.springpracticerestmvc.services.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of the CustomerService interface for managing customer-related operations.
 * Provides methods for CRUD operations on customers.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final Map<UUID, CustomerDTO> customerMap;

    /**
     * Constructor for CustomerServiceImpl.
     * Initializes the customerMap with sample customer data.
     */
    public CustomerServiceImpl() {
        var cst1 = new CustomerDTO(
                UUID.randomUUID(),
                "Customer 1",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        var cst2 = new CustomerDTO(
                UUID.randomUUID(),
                "Customer 2",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        var cst3 = new CustomerDTO(
                UUID.randomUUID(),
                "Customer 3",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        customerMap = new HashMap<>();
        customerMap.put(cst1.getId(), cst1);
        customerMap.put(cst2.getId(), cst1);
        customerMap.put(cst3.getId(), cst3);
    }

    /**
     * Retrieves all customers.
     *
     * @return A list of CustomerDTO objects representing all customers.
     */
    @Override
    public List<CustomerDTO> getAllCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param id The UUID of the customer to retrieve.
     * @return An Optional containing the CustomerDTO if found, or empty if not found.
     */
    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        return Optional.of(
                customerMap.get(id)
        );
    }

    /**
     * Saves a new customer.
     *
     * @param customerDTO The CustomerDTO containing details of the customer to save.
     * @return The saved CustomerDTO object.
     */
    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
        var savedCustomer = new CustomerDTO(
                UUID.randomUUID(),
                customerDTO.getName(),
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        customerMap.put(savedCustomer.getId(), savedCustomer);
        return savedCustomer;
    }

    /**
     * Updates an existing customer by their ID.
     *
     * @param customerId  The UUID of the customer to update.
     * @param customerDTO The CustomerDTO containing updated details for the customer.
     * @return An Optional containing the updated CustomerDTO.
     */
    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customerDTO) {
        var existingCustomer = customerMap.get(customerId);
        existingCustomer.setName(customerDTO.getName());
        return Optional.of(existingCustomer);
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param customerId The UUID of the customer to delete.
     * @return True if the customer was successfully deleted.
     */
    @Override
    public Boolean deleteCustomerById(UUID customerId) {
        customerMap.remove(customerId);
        return true;
    }

    /**
     * Partially updates a customer by their ID.
     *
     * @param customerId  The UUID of the customer to patch.
     * @param customerDTO The CustomerDTO containing the fields to update.
     * @return An Optional containing the patched CustomerDTO.
     */
    @Override
    public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customerDTO) {
        var existingCustomer = customerMap.get(customerId);

        if (StringUtils.hasText(customerDTO.getName())) {
            existingCustomer.setName(customerDTO.getName());
        }

        return Optional.of(existingCustomer);
    }
}