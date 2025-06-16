package com.example.springpracticerestmvc.repositories;

import com.example.springpracticerestmvc.entities.BeerAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerAuditRepository extends JpaRepository<BeerAudit, UUID> {
}
