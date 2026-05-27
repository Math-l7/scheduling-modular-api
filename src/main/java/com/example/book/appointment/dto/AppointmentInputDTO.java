package com.example.book.appointment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AppointmentInputDTO {
    private Integer businessId;
    private Integer staffId;
    private Integer serviceId;
    private LocalDateTime startTime;
}
