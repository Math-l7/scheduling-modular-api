package com.example.book.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.business.dto.BusinessInputDTO;
import com.example.book.business.dto.BusinessReturnDTO;
import com.example.book.business.dto.WorkingHoursInputDTO;
import com.example.book.business.dto.WorkingHoursReturnDTO;
import com.example.book.business.service.BusinessService;
import com.example.book.servicecatalog.dto.ServiceCatalogReturnDTO;
import com.example.book.servicecatalog.service.ServiceCatalogService;
import com.example.book.staff.dto.StaffReturnDTO;
import com.example.book.staff.service.StaffService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;
    private final ServiceCatalogService serviceCatalogService;
    private final StaffService staffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('OWNER')")
    public BusinessReturnDTO createBusiness(@RequestBody BusinessInputDTO input) {
        return businessService.createBusiness(input);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public BusinessReturnDTO getBusinessById(@PathVariable Integer id) {
        return businessService.getBusinessById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public BusinessReturnDTO getBusinessByName(@RequestParam String name) {
        return businessService.getBusinessByName(name);
    }

    @GetMapping("/services")
    @ResponseStatus(HttpStatus.OK)
    public List<ServiceCatalogReturnDTO> listActiveServices(@RequestParam String name) {
        return serviceCatalogService.findByBusiness(name);
    }

    @GetMapping("/{businessId}/staff/{staffId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('OWNER')")
    public StaffReturnDTO findByIdAndBusinessId(@PathVariable Integer staffId, @PathVariable Integer businessId) {
        return staffService.findByIdAndBusinessId(staffId, businessId);
    }

    @GetMapping("/{businessId}/staff")
    @ResponseStatus(HttpStatus.OK)
    public List<StaffReturnDTO> listActiveStaffByBusiness(@PathVariable Integer businessId) {
        return staffService.listActiveStaffByBusiness(businessId);
    }

    @PutMapping("/working-hours")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public WorkingHoursReturnDTO updateWorkingHours(@RequestBody WorkingHoursInputDTO input) {
        return businessService.updateWorkingHours(input);
    }

    @PutMapping("/{id}/desactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('OWNER')")
    public void desactivateBusiness(@PathVariable Integer id) {
        businessService.desactivateBusiness(id);
    }

    @PutMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('OWNER')")
    public void activateBusiness(@PathVariable Integer id) {
        businessService.activateBusiness(id);
    }

}
