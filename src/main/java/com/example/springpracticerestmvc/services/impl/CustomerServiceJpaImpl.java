package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.mappers.CustomerMapper;
import com.example.springpracticerestmvc.model.CustomerDTO;
import com.example.springpracticerestmvc.repositories.CustomerRepository;
import com.example.springpracticerestmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Implementation of the CustomerService interface using JPA for customer-related operations.
 * Provides methods for CRUD operations on customers and integrates caching.
 */
@Service
@Profile({"localdb"})
@Primary
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceJpaImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CacheManager cacheManager;

    /**
     * Clears cache entries for a specific customer ID and the customer list cache.
     *
     * @param customerId The UUID of the customer to clear cache for.
     */
    public void clearCache(UUID customerId) {
        if (cacheManager.getCache("customerListCache") != null) {
            cacheManager.getCache("customerListCache").clear();
        }

        if (cacheManager.getCache("customerCache") != null) {
            cacheManager.getCache("customerCache").evict(customerId);
        }
    }

    /**
     * Retrieves all customers.
     * Results are cached for performance optimization.
     *
     * @return A list of CustomerDTO objects representing all customers.
     */
    @Cacheable(cacheNames = "customerListCache")
    @Override
    public List<CustomerDTO> getAllCustomers() {
        log.info("in service - get all customers");
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::customerToCustomerDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a customer by their ID.
     * Results are cached for performance optimization.
     *
     * @param id The UUID of the customer to retrieve.
     * @return An Optional containing the CustomerDTO if found, or empty if not found.
     */
    @Cacheable(cacheNames = "customerCache", key = "#id")
    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        log.info("in service - get customer by id");
        return Optional.ofNullable(customerMapper.customerToCustomerDto(
                customerRepository.findById(id).orElse(null)
        ));
    }

    /**
     * Saves a new customer.
     * Clears the customer list cache after saving.
     *
     * @param customerDTO The CustomerDTO containing details of the customer to save.
     * @return The saved CustomerDTO object.
     */
    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
        if (cacheManager.getCache("customerListCache") != null) {
            cacheManager.getCache("customerListCache").clear();
        }
        return customerMapper.customerToCustomerDto(
                customerRepository.save(
                        customerMapper.customerDtoToCustomer(customerDTO)
                )
        );
    }

    /**
     * Updates an existing customer by their ID.
     * Clears the cache for the specific customer ID after updating.
     *
     * @param customerId  The UUID of the customer to update.
     * @param customerDTO The CustomerDTO containing updated details for the customer.
     * @return An Optional containing the updated CustomerDTO.
     */
    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customerDTO) {
        clearCache(customerId);
        AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();
        customerRepository.findById(customerId).ifPresentOrElse((foundCustomer) -> {
            foundCustomer.setName(customerDTO.getName());
            atomicReference.set(
                    Optional.of(
                            customerMapper.customerToCustomerDto(
                                    customerRepository.save(foundCustomer)
                            )
                    )
            );
        }, () -> atomicReference.set(Optional.empty()));
        return atomicReference.get();
    }

    /**
     * Deletes a customer by their ID.
     * Clears the cache for the specific customer ID after deletion.
     *
     * @param customerId The UUID of the customer to delete.
     * @return True if the customer was successfully deleted, false otherwise.
     */
    @Override
    public Boolean deleteCustomerById(UUID customerId) {
        clearCache(customerId);
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false;
    }

    /**
     * Partially updates a customer by their ID.
     * Clears the cache for the specific customer ID after patching.
     *
     * @param customerId  The UUID of the customer to patch.
     * @param customerDTO The CustomerDTO containing the fields to update.
     * @return An Optional containing the patched CustomerDTO.
     */
    @Override
    public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customerDTO) {
        clearCache(customerId);
        AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();
        customerRepository.findById(customerId).ifPresentOrElse((foundCustomer) -> {
            if (StringUtils.hasText(customerDTO.getName())) {
                foundCustomer.setName(customerDTO.getName());
            }
            atomicReference.set(
                    Optional.of(
                            customerMapper.customerToCustomerDto(
                                    customerRepository.save(foundCustomer)
                            )
                    )
            );
        }, () -> atomicReference.set(Optional.empty()));
        return atomicReference.get();
    }
}