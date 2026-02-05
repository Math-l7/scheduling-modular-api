package com.example.book.servicecatalog.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceCatalogInputDTO {
    private String name;
    private Integer duration;
    private BigDecimal price;
    private Integer businessId;
}
