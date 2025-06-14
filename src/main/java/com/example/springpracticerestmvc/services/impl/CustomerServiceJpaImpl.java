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

@Service
@Profile({"localdb"})
@Primary
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceJpaImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CacheManager cacheManager;

    public void clearCache(UUID customerId) {

        if (cacheManager.getCache("customerListCache") != null) {
            cacheManager.getCache("customerListCache").clear();
        }

        if (cacheManager.getCache("customerCache") != null) {
            cacheManager.getCache("customerCache").evict(customerId);
        }

    }

    @Cacheable(cacheNames = "customerListCache")
    @Override
    public List<CustomerDTO> getAllCustomers() {

        log.info("in service - get all customers");

        return customerRepository.findAll()
                .stream()
                .map(customerMapper::customerToCustomerDto)
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "customerCache", key = "#id")
    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {

        log.info("in service - get customer by id");

        return Optional.ofNullable(customerMapper.customerToCustomerDto(
                customerRepository.findById(id).orElse(null)
        ));
    }

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

    @Override
    public Boolean deleteCustomerById(UUID customerId) {

        clearCache(customerId);

        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }

        return false;
    }

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
