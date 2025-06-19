package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.mappers.BeerOrderMapper;
import com.example.springpracticerestmvc.model.BeerOrderDTO;
import com.example.springpracticerestmvc.repositories.BeerOrderRepository;
import com.example.springpracticerestmvc.services.BeerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public Optional<BeerOrderDTO> getById(UUID beerOrderId) {
        return Optional.ofNullable(
                beerOrderMapper.beerOrderToBeerOrderDTO(
                        beerOrderRepository.findById(beerOrderId).orElse(null)
                )
        );
    }

    @Override
    public Page<BeerOrderDTO> listOrders(Integer pageNumber, Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }

        if (pageSize == null || pageSize < 0) {
            pageSize = 25; // Default page size
        }

        var pageRequest = PageRequest.of(pageNumber, pageSize);

        return beerOrderRepository
                .findAll(pageRequest)
                .map(beerOrderMapper::beerOrderToBeerOrderDTO);
    }
}
