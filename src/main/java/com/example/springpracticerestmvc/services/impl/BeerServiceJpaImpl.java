package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.events.BeerCreatedEvent;
import com.example.springpracticerestmvc.events.BeerDeletedEvent;
import com.example.springpracticerestmvc.events.BeerPatchedEvent;
import com.example.springpracticerestmvc.events.BeerUpdatedEvent;
import com.example.springpracticerestmvc.mappers.BeerMapper;
import com.example.springpracticerestmvc.model.BeerDTO;
import com.example.springpracticerestmvc.model.BeerStyle;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.example.springpracticerestmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of the BeerService interface using JPA for beer-related operations.
 * Provides methods for CRUD operations on beers and integrates caching and event publishing.
 */
@Service
@Profile({"localdb"})
@Primary
@RequiredArgsConstructor
@Slf4j
public class BeerServiceJpaImpl implements BeerService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 25;
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Lists beers with optional filtering and pagination.
     * Results are cached for performance optimization.
     *
     * @param beerName      The name of the beer to filter by (optional).
     * @param beerStyle     The style of the beer to filter by (optional).
     * @param showInventory Whether to show inventory details (optional).
     * @param pageNumber    The page number to retrieve (1-based).
     * @param pageSize      The number of items per page.
     * @return A Page of BeerDTO objects.
     */
    @Cacheable(cacheNames = "beerListCache")
    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        log.info("List beers - in JPA Service");

        Page<Beer> beerPage;
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

        if (StringUtils.hasText(beerName) && beerStyle == null) {
            beerPage = listBeersByName(beerName, pageRequest);
        } else if (!StringUtils.hasText(beerName) && beerStyle != null) {
            beerPage = listBeersByStyle(beerStyle, pageRequest);
        } else if (StringUtils.hasText(beerName) && beerStyle != null) {
            beerPage = listBeersByNameAndStyle(beerName, beerStyle, pageRequest);
        } else {
            beerPage = beerRepository.findAll(pageRequest);
        }

        if (showInventory != null && !showInventory) {
            beerPage.forEach(beer -> beer.setQuantityOnHand(null));
        }

        return beerPage.map(beerMapper::beerToBeerDto);
    }

    /**
     * Builds a PageRequest object for pagination.
     *
     * @param pageNumber The page number to retrieve (1-based).
     * @param pageSize   The number of items per page.
     * @return A PageRequest object.
     */
    private PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber = (pageNumber != null && pageNumber > 0) ? pageNumber - 1 : DEFAULT_PAGE;
        int queryPageSize = (pageSize == null) ? DEFAULT_PAGE_SIZE : Math.min(pageSize, 1000);
        Sort sort = Sort.by(Sort.Order.asc("beerName"));
        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }

    /**
     * Lists beers filtered by name and style.
     *
     * @param beerName  The name of the beer to filter by.
     * @param beerStyle The style of the beer to filter by.
     * @param pageable  The pagination information.
     * @return A Page of Beer entities.
     */
    private Page<Beer> listBeersByNameAndStyle(String beerName, BeerStyle beerStyle, Pageable pageable) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle, pageable);
    }

    /**
     * Lists beers filtered by style.
     *
     * @param beerStyle The style of the beer to filter by.
     * @param pageable  The pagination information.
     * @return A Page of Beer entities.
     */
    private Page<Beer> listBeersByStyle(BeerStyle beerStyle, Pageable pageable) {
        return beerRepository.findAllByBeerStyle(beerStyle, pageable);
    }

    /**
     * Lists beers filtered by name.
     *
     * @param beerName The name of the beer to filter by.
     * @param pageable The pagination information.
     * @return A Page of Beer entities.
     */
    private Page<Beer> listBeersByName(String beerName, Pageable pageable) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageable);
    }

    /**
     * Clears cache entries for a specific beer ID.
     *
     * @param beerId The UUID of the beer to clear cache for.
     */
    private void clearCache(UUID beerId) {
        if (cacheManager.getCache("beerCache") != null) {
            cacheManager.getCache("beerCache").evict(beerId);
        }
        if (cacheManager.getCache("beerListCache") != null) {
            cacheManager.getCache("beerListCache").clear();
        }
    }

    /**
     * Retrieves a beer by its ID.
     * Results are cached for performance optimization.
     *
     * @param beerId The UUID of the beer to retrieve.
     * @return An Optional containing the BeerDTO if found, or empty if not found.
     */
    @Cacheable(cacheNames = "beerCache", key = "#beerId")
    @Override
    public Optional<BeerDTO> getBeerById(UUID beerId) {
        log.info("Get beer by id - in JPA Service");
        clearCache(beerId);
        return Optional.ofNullable(beerMapper.beerToBeerDto(beerRepository.findById(beerId).orElse(null)));
    }

    /**
     * Saves a new beer and publishes a BeerCreatedEvent.
     *
     * @param beerDTO The BeerDTO containing details of the beer to save.
     * @return The saved BeerDTO object.
     */
    @Override
    public BeerDTO saveNewBeer(BeerDTO beerDTO) {
        if (cacheManager.getCache("beerListCache") != null) {
            cacheManager.getCache("beerListCache").clear();
        }

        val savedBeer = beerRepository.save(beerMapper.beerdtoToBeer(beerDTO));
        val auth = SecurityContextHolder.getContext().getAuthentication();
        applicationEventPublisher.publishEvent(new BeerCreatedEvent(savedBeer, auth));
        return beerMapper.beerToBeerDto(savedBeer);
    }

    /**
     * Updates an existing beer by its ID and publishes a BeerUpdatedEvent.
     *
     * @param beerId  The UUID of the beer to update.
     * @param beerDTO The BeerDTO containing updated details for the beer.
     * @return An Optional containing the updated BeerDTO.
     */
    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beerDTO) {
        clearCache(beerId);
        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse((foundBeer) -> {
            foundBeer.setBeerName(beerDTO.getBeerName());
            foundBeer.setBeerStyle(beerDTO.getBeerStyle());
            foundBeer.setUpc(beerDTO.getUpc());
            foundBeer.setPrice(beerDTO.getPrice());
            foundBeer.setQuantityOnHand(beerDTO.getQuantityOnHand());
            foundBeer.setVersion(beerDTO.getVersion());

            val savedBeer = beerRepository.save(foundBeer);
            val auth = SecurityContextHolder.getContext().getAuthentication();
            applicationEventPublisher.publishEvent(new BeerUpdatedEvent(savedBeer, auth));
            atomicReference.set(Optional.of(beerMapper.beerToBeerDto(savedBeer)));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    /**
     * Deletes a beer by its ID and publishes a BeerDeletedEvent.
     *
     * @param beerId The UUID of the beer to delete.
     * @return True if the beer was successfully deleted, false otherwise.
     */
    @Override
    public Boolean deleteById(UUID beerId) {
        clearCache(beerId);

        if (beerRepository.existsById(beerId)) {
            val auth = SecurityContextHolder.getContext().getAuthentication();
            applicationEventPublisher.publishEvent(new BeerDeletedEvent(Beer.builder().id(beerId).build(), auth));
            beerRepository.deleteById(beerId);
            return true;
        }

        return false;
    }

    /**
     * Partially updates a beer by its ID and publishes a BeerPatchedEvent.
     *
     * @param beerId  The UUID of the beer to patch.
     * @param beerDTO The BeerDTO containing the fields to update.
     * @return An Optional containing the patched BeerDTO.
     */
    @Override
    public Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beerDTO) {
        clearCache(beerId);
        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse((foundBeer) -> {
            if (StringUtils.hasText(beerDTO.getBeerName())) {
                foundBeer.setBeerName(beerDTO.getBeerName());
            }
            if (beerDTO.getBeerStyle() != null) {
                foundBeer.setBeerStyle(beerDTO.getBeerStyle());
            }
            if (StringUtils.hasText(beerDTO.getUpc())) {
                foundBeer.setUpc(beerDTO.getUpc());
            }
            if (beerDTO.getPrice() != null) {
                foundBeer.setPrice(beerDTO.getPrice());
            }
            if (beerDTO.getQuantityOnHand() != null) {
                foundBeer.setQuantityOnHand(beerDTO.getQuantityOnHand());
            }

            val savedBeer = beerRepository.save(foundBeer);
            val auth = SecurityContextHolder.getContext().getAuthentication();
            applicationEventPublisher.publishEvent(new BeerPatchedEvent(savedBeer, auth));
            atomicReference.set(Optional.of(beerMapper.beerToBeerDto(savedBeer)));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }
}