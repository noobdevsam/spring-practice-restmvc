package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.mappers.CustomerMapper;
import com.example.springpracticerestmvc.model.CustomerDTO;
import com.example.springpracticerestmvc.repositories.CustomerRepository;
import com.example.springpracticerestmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
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
@Profile("jpa")
@Primary
@RequiredArgsConstructor
public class CustomerServiceJpaImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::customerToCustomerDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        return Optional.ofNullable(customerMapper.customerToCustomerDto(
                customerRepository.findById(id).orElse(null)
        ));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
        return customerMapper.customerToCustomerDto(
                customerRepository.save(
                        customerMapper.customerDtoToCustomer(customerDTO)
                )
        );
    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customerDTO) {
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
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }

        return false;
    }

    @Override
    public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customerDTO) {
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
