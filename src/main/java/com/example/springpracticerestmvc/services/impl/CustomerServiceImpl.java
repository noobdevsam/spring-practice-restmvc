package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.model.Customer;
import com.example.springpracticerestmvc.services.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    private Map<UUID, Customer> customerMap;

    public CustomerServiceImpl() {
        var cst1 = new Customer(
                UUID.randomUUID(),
                "Customer 1",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
                );

        var cst2 = new Customer(
                UUID.randomUUID(),
                "Customer 2",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
                );

        var cst3 = new Customer(
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
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Customer getCustomerById(UUID id) {
        return customerMap.get(id);
    }

    @Override
    public Customer saveNewCustomer(Customer customer) {
        var savedCustomer = new Customer(
                UUID.randomUUID(),
                customer.getName(),
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
                );

        customerMap.put(savedCustomer.getId(), savedCustomer);
        return savedCustomer;
    }

    @Override
    public void updateCustomerById(UUID customerId, Customer customer) {
        var existingCustomer = customerMap.get(customerId);
        existingCustomer.setName(customer.getName());
    }

    @Override
    public void deleteCustomerById(UUID customerId) {
        customerMap.remove(customerId);
    }

    @Override
    public void pathCustomerById(UUID customerId, Customer customer) {
        var existingCustomer = customerMap.get(customerId);

        if (StringUtils.hasText(customer.getName())) {
            existingCustomer.setName(customer.getName());
        }
    }
}
