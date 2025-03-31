package com.example.springpracticerestmvc.repositories;

import com.example.springpracticerestmvc.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}