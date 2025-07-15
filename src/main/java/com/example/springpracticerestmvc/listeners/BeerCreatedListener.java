package com.example.springpracticerestmvc.listeners;

import com.example.springpracticerestmvc.events.*;
import com.example.springpracticerestmvc.mappers.BeerMapper;
import com.example.springpracticerestmvc.repositories.BeerAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener for beer-related events. This class listens to various beer events
 * and processes them asynchronously to create audit records.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BeerCreatedListener {

    // Mapper to convert Beer objects to BeerAudit objects
    private final BeerMapper beerMapper;

    // Repository to save BeerAudit records
    private final BeerAuditRepository beerAuditRepository;

    /**
     * Handles beer-related events asynchronously. Depending on the type of event,
     * it creates an audit record and saves it to the database.
     *
     * @param event The beer event to process. Can be one of BeerCreatedEvent, BeerUpdatedEvent,
     *              BeerPatchedEvent, or BeerDeletedEvent.
     */
    @Async
    @EventListener
    public void listen(BeerEvent event) {

        // Map the Beer object from the event to a BeerAudit object
        val beerAudit = beerMapper.beerToBeerAudit(event.getBeer());
        String eventType = null;

        // Determine the type of event and set the corresponding audit event type
        switch (event) {
            case BeerCreatedEvent beerCreatedEvent -> eventType = "BEER_CREATED";
            case BeerUpdatedEvent beerUpdatedEvent -> eventType = "BEER_UPDATED";
            case BeerPatchedEvent beerPatchedEvent -> eventType = "BEER_PATCHED";
            case BeerDeletedEvent beerDeletedEvent -> eventType = "BEER_DELETED";
            default -> eventType = "UNKNOWN";
        }

        beerAudit.setAuditEventType(eventType);

        // Set the principal name if authentication information is available
        if (event.getAuthentication() != null && event.getAuthentication().getName() != null) {
            beerAudit.setPrincipalName(event.getAuthentication().getName());
        }

        // Save the audit record to the repository
        val savedBeerAudit = beerAuditRepository.save(beerAudit);

        // Log the saved audit record
        log.info("BeerAudit saved: {}  for Id: {}", eventType, savedBeerAudit.getId());
    }
}