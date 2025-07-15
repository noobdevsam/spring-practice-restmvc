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

/**
 * Implementation of the BeerOrderService interface for managing beer orders.
 * Provides methods for CRUD operations on beer orders.
 */
@Service
@RequiredArgsConstructor
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerRepository beerRepository;
    private final BeerOrderMapper beerOrderMapper;

    /**
     * Retrieves a beer order by its ID.
     *
     * @param beerOrderId The UUID of the beer order.
     * @return An Optional containing the BeerOrderDTO if found, or empty if not found.
     */
    @Override
    public Optional<BeerOrderDTO> getById(UUID beerOrderId) {
        return Optional.ofNullable(
                beerOrderMapper.beerOrderToBeerOrderDTO(
                        beerOrderRepository.findById(beerOrderId).orElse(null)
                )
        );
    }

    /**
     * Lists beer orders with pagination.
     *
     * @param pageNumber The page number to retrieve (0-based).
     * @param pageSize   The number of items per page.
     * @return A Page of BeerOrderDTO objects.
     */
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

    /**
     * Creates a new beer order.
     *
     * @param beerOrderCreateDTO The DTO containing details for the new beer order.
     * @return The created BeerOrder entity.
     */
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

    /**
     * Updates an existing beer order.
     *
     * @param beerOrderId        The UUID of the beer order to update.
     * @param beerOrderUpdateDTO The DTO containing updated details for the beer order.
     * @return The updated BeerOrderDTO.
     */
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

    /**
     * Deletes a beer order by its ID.
     *
     * @param beerOrderId The UUID of the beer order to delete.
     * @throws NotFoundException If the beer order does not exist.
     */
    @Override
    public void deleteOrder(UUID beerOrderId) {

        if (beerOrderRepository.existsById(beerOrderId)) {
            beerOrderRepository.deleteById(beerOrderId);
        } else {
            throw new NotFoundException();
        }
    }

}