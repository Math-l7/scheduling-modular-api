package com.example.book.business.repository;

import java.time.DayOfWeek;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.book.business.model.WorkingHours;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Integer> {

        Optional<WorkingHours> findByBusinessIdAndDayOfWeek(
                        Integer businessId,
                        DayOfWeek dayOfWeek);

}
