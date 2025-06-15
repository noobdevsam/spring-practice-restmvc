package com.example.springpracticerestmvc.listeners;

import com.example.springpracticerestmvc.events.BeerCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BeerCreatedListener {

    @EventListener
    public void listen(BeerCreatedEvent event) {
        System.out.println("I heard a beer was created!");
        System.out.println("Beer Id: " + event.getBeer().getId());

        System.out.println("Inside BeerCreatedListener");
        System.out.println("Current thread name = " + Thread.currentThread().getName());
        System.out.println("Current thread id = " + Thread.currentThread().threadId());

        // TODO: Implement - add real implementation to persist audit record
    }
}
