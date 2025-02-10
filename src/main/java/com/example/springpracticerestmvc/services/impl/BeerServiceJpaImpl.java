package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.mappers.BeerMapper;
import com.example.springpracticerestmvc.model.BeerDTO;
import com.example.springpracticerestmvc.repositories.BeerRepository;
import com.example.springpracticerestmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

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

    @Override
    public List<BeerDTO> listBeers() {
        return beerRepository.findAll()
                .stream()
                .map(beerMapper::beerToBeerDto)
                .collect(Collectors.toList());
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
    public void patchBeerById(UUID beerId, BeerDTO beerDTO) {

    }
}
