package com.example.book.business.model;

import java.util.List;

import com.example.book.business.enums.BusinessType;
import com.example.book.servicecatalog.model.ServiceCatalog;
import com.example.book.staff.model.Staff;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Business {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessType type;

    @OneToMany(mappedBy = "business")
    private List<WorkingHours> workingHours;

    @OneToMany(mappedBy = "business")
    private List<Staff> staff;

    @OneToMany(mappedBy = "business")
    private List<ServiceCatalog> services;

    private boolean active = true;

    public Business(String name, BusinessType type) {
        this.name = name;
        this.type = type;
    }

}
