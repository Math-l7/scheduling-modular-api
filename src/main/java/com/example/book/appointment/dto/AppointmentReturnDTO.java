package com.example.book.appointment.dto;

import java.time.LocalDateTime;

import com.example.book.appointment.enums.AppointmentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentReturnDTO {
    private Integer appointmentId;
    private String businessName;
    private Integer businessId;
    private Integer serviceId;
    private String serviceName;
    private Integer staffId;
    private String staffName;
    private Integer clientId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
}
