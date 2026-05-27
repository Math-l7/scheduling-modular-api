package com.example.book.appointment.policy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.example.book.appointment.model.Appointment;
import com.example.book.business.model.Business;
import com.example.book.business.model.WorkingHours;
import com.example.book.servicecatalog.model.ServiceCatalog;
import com.example.book.staff.model.Staff;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AppointmentContext {
    private Business business;
    private Staff staff;
    private ServiceCatalog service;
    private LocalDateTime start;
    private LocalDateTime end;
    private WorkingHours workingHours;
    private List<Appointment> staffAppointments;

    public boolean hasConflict() {
        return staffAppointments.stream()
                .anyMatch(a -> start.isBefore(a.getEndTime()) && end.isAfter(a.getStartTime()));
    }

    public boolean isWithinWorkinHours() {
        return !start.toLocalTime().isBefore(workingHours.getStartTime())
                && !end.toLocalTime().isAfter(workingHours.getEndTime());
    }

    public boolean isInThePast() {
        return start.isAfter(LocalDateTime.now());
    }

    public boolean isDurationValid() {
        Long minutes = Duration.between(start, end).toMinutes();
        Long duration = (long) service.getDuration();
        return minutes == duration;
    }

    public boolean sameBusiness() {
        return staff.getBusiness().getId().equals(business.getId())
                && service.getBusiness().getId().equals(business.getId());
    }
}
