package com.example.book.staff.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.staff.dto.StaffInputDTO;
import com.example.book.staff.dto.StaffReturnDTO;
import com.example.book.staff.service.StaffService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('OWNER')")
    public StaffReturnDTO createStaff(@RequestBody StaffInputDTO input) {
        return staffService.createStaff(input);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public StaffReturnDTO findById(@PathVariable Integer id) {
        return staffService.findById(id);
    }

    @PutMapping("/{staffId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('OWNER')")
    public void activateStaff(@PathVariable Integer staffId) {
        staffService.activateStaff(staffId);
    }

    @PutMapping("/{staffId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('OWNER')")
    public void desactivateStaff(@PathVariable Integer staffId) {
        staffService.deactivateStaff(staffId);
    }

}
