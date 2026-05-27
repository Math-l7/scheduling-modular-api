package com.example.book.appointment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.appointment.dto.AppointmentInputDTO;
import com.example.book.appointment.dto.AppointmentReturnDTO;
import com.example.book.appointment.service.AppointmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

@RequestMapping("/appointments")
@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentReturnDTO createAppointment(@RequestBody AppointmentInputDTO input) {
        return appointmentService.createAppointment(input);
    }

    @PutMapping("/{appointmentId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelAppointment(@PathVariable Integer appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
    }

    @PutMapping("/{appointmentId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('STAFF')")
    public void completeAppointment(@PathVariable Integer appointmentId) {
        appointmentService.completeAppointment(appointmentId);
    }

    @GetMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.OK)
    public AppointmentReturnDTO getAppointmentById(@PathVariable Integer appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    @GetMapping("/business/{businessId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('STAFF', 'OWNER')")
    public List<AppointmentReturnDTO> listByBusiness(@PathVariable Integer businessId) {
        return appointmentService.listByBusiness(businessId);
    }

    @GetMapping("/staff")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('STAFF', 'OWNER')")
    public List<AppointmentReturnDTO> listByStaff() {
        return appointmentService.listByStaff();
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public List<AppointmentReturnDTO> listByClient() {
        return appointmentService.listByClient();
    }

}
