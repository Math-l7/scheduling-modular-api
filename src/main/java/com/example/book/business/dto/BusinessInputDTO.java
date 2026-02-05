package com.example.book.business.dto;

import com.example.book.business.enums.BusinessType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BusinessInputDTO {
    private String name;
    private BusinessType type;
}
