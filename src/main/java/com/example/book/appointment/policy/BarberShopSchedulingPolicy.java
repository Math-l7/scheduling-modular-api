package com.example.book.appointment.policy;

import org.springframework.stereotype.Component;

import com.example.book.common.exceptions.BusinessException;

@Component
public class BarberShopSchedulingPolicy implements SchedulingPolicy {

    @Override
    public void validateSchedule(AppointmentContext context) {
        if (context.hasConflict()) {
            throw new BusinessException("Horário indisponível.");
        }

        if (!context.isWithinWorkinHours()) {
            throw new BusinessException("Fora do horário de funcionamento.");
        }

        if (context.isInThePast()) {
            throw new BusinessException("Não é possível agendar no passado.");
        }

        if (!context.isDurationValid()) {
            throw new BusinessException("Duração inválida.");
        }

        if (!context.sameBusiness()) {
            throw new BusinessException("Business inválido.");
        }
    }
}
