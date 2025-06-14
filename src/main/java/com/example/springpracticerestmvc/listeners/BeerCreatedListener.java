package com.example.springpracticerestmvc.listeners;

import com.example.springpracticerestmvc.events.BeerCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class BeerCreatedListener {

    public void listen(BeerCreatedEvent event) {
        System.out.println("I heard a beer was created!");
        System.out.println("Beer Id: " + event.getBeer().getId());

        // TODO: Implement - add real implementation to persist audit record
    }
}
