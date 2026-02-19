package com.example.book.appointment.model;

import java.time.LocalDateTime;

import com.example.book.appointment.enums.AppointmentStatus;
import com.example.book.business.model.Business;
import com.example.book.servicecatalog.model.ServiceCatalog;
import com.example.book.staff.model.Staff;
import com.example.book.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "businessId", nullable = false)
    private Business business;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceCatalog service;

    public Appointment(Business business, ServiceCatalog service, Staff staff, User client,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        this.business = business;
        this.service = service;
        this.staff = staff;
        this.client = client;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
