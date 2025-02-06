package com.example.springpracticerestmvc.mappers;

import com.example.springpracticerestmvc.entities.Customer;
import com.example.springpracticerestmvc.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDTO customerDTO);

    CustomerDTO customerToCustomerDto(Customer customer);
}
