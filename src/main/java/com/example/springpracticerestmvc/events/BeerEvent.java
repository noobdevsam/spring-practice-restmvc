package com.example.springpracticerestmvc.events;

import com.example.springpracticerestmvc.entities.Beer;
import org.springframework.security.core.Authentication;

public interface BeerEvent {

    Beer getBeer();

    Authentication getAuthentication();
}
