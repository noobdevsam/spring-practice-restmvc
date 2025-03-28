package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.mappers.BeerMapper;
import com.example.springpracticerestmvc.model.BeerDTO;
import com.example.springpracticerestmvc.model.BeerStyle;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.example.springpracticerestmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
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
public class BeerServiceJpaImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 25;

    @Override
    public List<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {

        List<Beer> beerList;
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

        if (StringUtils.hasText(beerName) && beerStyle == null) {
            beerList = listBeersByName(beerName);
        } else if (!StringUtils.hasText(beerName) && beerStyle != null) {
            beerList = listBeersByStyle(beerStyle);
        } else if (StringUtils.hasText(beerName) && beerStyle != null) {
            beerList = listBeersByNameAndStyle(beerName, beerStyle);
        } else {
            beerList = beerRepository.findAll();
        }

        if (showInventory != null && !showInventory) {
            beerList.forEach(beer -> beer.setQuantityOnHand(null));
        }

        return beerList
                .stream()
                .map(beerMapper::beerToBeerDto)
                .collect(Collectors.toList());
    }

    private PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {

        int queryPageNumber;
        int queryPageSize;

        if (pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber;
        } else {
            queryPageNumber = DEFAULT_PAGE;
        }

        if (pageSize == null) {
            queryPageSize = 1000;
        } else {
            if (pageSize > 1000) {
                queryPageSize = 1000;
            } else {
                queryPageSize = pageSize;
            }
        }

        return PageRequest.of(queryPageNumber, queryPageSize);
    }

    private List<Beer> listBeersByNameAndStyle(String beerName, BeerStyle beerStyle) {
        return beerRepository
                .findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle);
    }

    private List<Beer> listBeersByStyle(BeerStyle beerStyle) {
        return beerRepository.findAllByBeerStyle(beerStyle);
    }

    public List<Beer> listBeersByName(String beerName) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%");
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID beerId) {
        return Optional.ofNullable(
                beerMapper.beerToBeerDto(beerRepository.findById(beerId).orElse(null))
        );
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO beerDTO) {
        return beerMapper.beerToBeerDto(
                beerRepository.save(beerMapper.beerdtoToBeer(beerDTO))
        );
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beerDTO) {

        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse((foundBeer) -> {
            foundBeer.setBeerName(beerDTO.getBeerName());
            foundBeer.setBeerStyle(beerDTO.getBeerStyle());
            foundBeer.setUpc(beerDTO.getUpc());
            foundBeer.setPrice(beerDTO.getPrice());
            foundBeer.setQuantityOnHand(beerDTO.getQuantityOnHand());
            //beerRepository.save(foundBeer);
            atomicReference.set(Optional.of(
                    beerMapper.beerToBeerDto(beerRepository.save(foundBeer))
            ));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    @Override
    public Boolean deleteById(UUID beerId) {
        if (beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }

        return false;
    }

    @Override
    public Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beerDTO) {
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

            atomicReference.set(Optional.of(
                    beerMapper.beerToBeerDto(
                            beerRepository.save(foundBeer)
                    )
            ));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }
}
