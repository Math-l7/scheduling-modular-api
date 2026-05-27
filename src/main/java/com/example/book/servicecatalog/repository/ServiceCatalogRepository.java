package com.example.book.servicecatalog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.book.servicecatalog.model.ServiceCatalog;

public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Integer> {

    List<ServiceCatalog> findByBusinessId(Integer businessId);

    boolean existsByNameAndBusinessId(String name, Integer businessId);

}
