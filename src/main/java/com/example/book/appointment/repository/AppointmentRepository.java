package com.example.book.appointment.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.book.appointment.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
        boolean existsByStaffIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        Integer staffId,
                        LocalDateTime endTime,
                        LocalDateTime startTime);

        public boolean existsByServiceId(Integer serviceId);

        List<Appointment> findByBusinessId(Integer businessId);

        List<Appointment> findByStaffId(Integer staffId);

        List<Appointment> findByStaffIdAndStaffActiveTrue(Integer staffId);

        List<Appointment> findByClientId(Integer clientId);

}
