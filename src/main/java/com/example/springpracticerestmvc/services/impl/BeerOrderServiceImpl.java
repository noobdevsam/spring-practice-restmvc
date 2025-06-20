package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.entities.BeerOrder;
import com.example.springpracticerestmvc.entities.BeerOrderLine;
import com.example.springpracticerestmvc.entities.BeerOrderShipment;
import com.example.springpracticerestmvc.exceptions.NotFoundException;
import com.example.springpracticerestmvc.mappers.BeerOrderMapper;
import com.example.springpracticerestmvc.model.BeerOrderCreateDTO;
import com.example.springpracticerestmvc.model.BeerOrderDTO;
import com.example.springpracticerestmvc.model.BeerOrderUpdateDTO;
import com.example.springpracticerestmvc.repositories.BeerOrderRepository;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.example.springpracticerestmvc.repositories.CustomerRepository;
import com.example.springpracticerestmvc.services.BeerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerRepository beerRepository;
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

    @Override
    @Transactional
    public BeerOrder createOrder(BeerOrderCreateDTO beerOrderCreateDTO) {

        var customer = customerRepository.findById(beerOrderCreateDTO.getCustomerId())
                .orElseThrow(NotFoundException::new);
        Set<BeerOrderLine> beerOrderLines = new HashSet<>();

        beerOrderCreateDTO.getBeerOrderLines().forEach(beerOrderLine -> {
            beerOrderLines.add(
                    BeerOrderLine.builder()
                            .beer(
                                    beerRepository.findById(beerOrderLine.getBeerId())
                                            .orElseThrow(NotFoundException::new)
                            )
                            .orderQuantity(beerOrderLine.getOrderQuantity())
                            .build()
            );
        });

        return beerOrderRepository.save(
                BeerOrder.builder()
                        .customer(customer)
                        .beerOrderLines(beerOrderLines)
                        .customerRef(beerOrderCreateDTO.getCustomerRef())
                        .build()
        );
    }

    @Transactional
    @Override
    public BeerOrderDTO updateOrder(UUID beerOrderId, BeerOrderUpdateDTO beerOrderUpdateDTO) {

        var order = beerOrderRepository.findById(beerOrderId)
                .orElseThrow(NotFoundException::new);

        order.setCustomer(
                customerRepository.findById(
                        beerOrderUpdateDTO.getCustomerId()
                ).orElseThrow(
                        NotFoundException::new
                )
        );
        order.setCustomerRef(beerOrderUpdateDTO.getCustomerRef());

        beerOrderUpdateDTO.getBeerOrderLines().forEach(beerOrderLine -> {

            if (beerOrderLine.getBeerId() != null) {

                var foundLine = order.getBeerOrderLines().stream()
                        .filter(
                                line1 -> line1.getId().equals(beerOrderLine.getId())
                        )
                        .findFirst()
                        .orElseThrow(NotFoundException::new);

                foundLine.setBeer(
                        beerRepository.findById(beerOrderLine.getBeerId())
                                .orElseThrow(NotFoundException::new)
                );
                foundLine.setOrderQuantity(
                        beerOrderLine.getOrderQuantity()
                );
                foundLine.setQuantityAllocated(
                        beerOrderLine.getQuantityAllocated()
                );

            } else {

                order.getBeerOrderLines().add(
                        BeerOrderLine.builder()
                                .beer(
                                        beerRepository.findById(beerOrderLine.getBeerId())
                                                .orElseThrow(NotFoundException::new)
                                )
                                .orderQuantity(beerOrderLine.getOrderQuantity())
                                .quantityAllocated(beerOrderLine.getQuantityAllocated())
                                .build()
                );
            }
        });

        if (
                beerOrderUpdateDTO.getBeerOrderShipment() != null &&
                        beerOrderUpdateDTO.getBeerOrderShipment().getTrackingNumber() != null
        ) {
            if (order.getBeerOrderShipment() == null) {
                order.setBeerOrderShipment(
                        BeerOrderShipment.builder()
                                .trackingNumber(beerOrderUpdateDTO.getBeerOrderShipment().getTrackingNumber())
                                .build()
                );
            }
        }

        return beerOrderMapper.beerOrderToBeerOrderDTO(
                beerOrderRepository.save(order)
        );
    }
}
