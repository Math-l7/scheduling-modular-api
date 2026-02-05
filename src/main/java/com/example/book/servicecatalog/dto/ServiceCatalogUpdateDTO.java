package com.example.book.servicecatalog.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceCatalogUpdateDTO {
    private String name;
    private Integer durationMinutes;
    private BigDecimal price;
}
