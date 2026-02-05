package com.example.book.servicecatalog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.servicecatalog.dto.ServiceCatalogInputDTO;
import com.example.book.servicecatalog.dto.ServiceCatalogReturnDTO;
import com.example.book.servicecatalog.dto.ServiceCatalogUpdateDTO;
import com.example.book.servicecatalog.service.ServiceCatalogService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('OWNER','STAFF')")
    public ServiceCatalogReturnDTO createServiceCatalog(@RequestBody ServiceCatalogInputDTO input) {
        return serviceCatalogService.createServiceCatalog(input);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ServiceCatalogReturnDTO findById(@PathVariable Integer id) {
        return serviceCatalogService.findById(id);
    }

    @PutMapping("/{serviceId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('OWNER','STAFF')")
    public ServiceCatalogReturnDTO updateServiceCatalog(@PathVariable Integer serviceId,
            @RequestBody ServiceCatalogUpdateDTO dto) {
        return serviceCatalogService.updateServiceCatalog(serviceId, dto);
    }

    @PutMapping("/{id}/desactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public void desactivateServiceCatalog(@PathVariable Integer id) {
        serviceCatalogService.desactivateServiceCatalog(id);
    }

    @PutMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public void activateServiceCatalog(@PathVariable Integer id) {
        serviceCatalogService.activateServiceCatalog(id);
    }

}
