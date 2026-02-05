package com.example.book.business.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.book.business.model.Business;

public interface BusinessRepository extends JpaRepository<Business, Integer> {

    public boolean existsByName(String name);

    public Optional<Business> findByName(String name);

}
