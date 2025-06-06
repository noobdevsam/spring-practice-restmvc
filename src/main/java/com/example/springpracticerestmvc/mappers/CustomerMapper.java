package com.example.springpracticerestmvc.mappers;

import com.example.springpracticerestmvc.entities.Customer;
import com.example.springpracticerestmvc.model.CustomerDTO;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Mapper(componentModel = "spring")
@Profile({"localdb", "default"})
@Primary
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDTO customerDTO);

    CustomerDTO customerToCustomerDto(Customer customer);
}
