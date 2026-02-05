package com.example.book.appointment.policy;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppointmentValidator {

    private final BarberShopSchedulingPolicy barberShopSchedulingPolicy;

    public void validateCreation(AppointmentContext context) {
        barberShopSchedulingPolicy.validateSchedule(context);
    }

}
