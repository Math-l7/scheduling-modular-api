package com.example.book.servicecatalog.model;

import java.math.BigDecimal;

import com.example.book.business.model.Business;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCatalog {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    private boolean activate = true;

    public ServiceCatalog(String name, Integer duration, BigDecimal price, Business business) {
        this.name = name;
        this.duration = duration;
        this.price = price;
        this.business = business;
    }

}
