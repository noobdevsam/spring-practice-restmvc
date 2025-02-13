package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.model.CustomerDTO;
import com.example.springpracticerestmvc.services.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Map<UUID, CustomerDTO> customerMap;

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

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        return Optional.of(
                customerMap.get(id)
        );
    }

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

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customerDTO) {
        var existingCustomer = customerMap.get(customerId);
        existingCustomer.setName(customerDTO.getName());
        return Optional.of(existingCustomer);
    }

    @Override
    public Boolean deleteCustomerById(UUID customerId) {
        customerMap.remove(customerId);
        return true;
    }

    @Override
    public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customerDTO) {
        var existingCustomer = customerMap.get(customerId);

        if (StringUtils.hasText(customerDTO.getName())) {
            existingCustomer.setName(customerDTO.getName());
        }

        return Optional.of(existingCustomer);
    }
}
