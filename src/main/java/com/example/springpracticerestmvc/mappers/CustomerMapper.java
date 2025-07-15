package com.example.springpracticerestmvc.mappers;

import com.example.springpracticerestmvc.entities.Customer;
import com.example.springpracticerestmvc.model.CustomerDTO;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Mapper interface for converting between Customer and CustomerDTO objects.
 * Utilizes MapStruct for object mapping and is configured as a Spring component.
 */
@Mapper(componentModel = "spring")
@Profile({"localdb", "default"})
@Primary
public interface CustomerMapper {

    /**
     * Maps a CustomerDTO object to a Customer entity.
     *
     * @param customerDTO The CustomerDTO object to be mapped.
     * @return The mapped Customer entity.
     */
    Customer customerDtoToCustomer(CustomerDTO customerDTO);

    /**
     * Maps a Customer entity to a CustomerDTO object.
     *
     * @param customer The Customer entity to be mapped.
     * @return The mapped CustomerDTO object.
     */
    CustomerDTO customerToCustomerDto(Customer customer);
}