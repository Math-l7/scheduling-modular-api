package com.example.book.staff.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.book.staff.model.Staff;

public interface StaffRepository extends JpaRepository<Staff, Integer> {

    List<Staff> findByBusinessId(Integer businessId);

    boolean existsByPublicNameAndBusinessId(String publicName, Integer businessId);

    List<Staff> findByBusinessIdAndActiveTrue(Integer businessId);

    boolean existsByIdAndBusinessId(Integer staffId, Integer businessId);

    Optional<Staff> findByIdAndBusinessId(Integer staffId, Integer businessId);

    boolean existsByUserIdAndBusinessId(Integer userId, Integer businessId);

}
