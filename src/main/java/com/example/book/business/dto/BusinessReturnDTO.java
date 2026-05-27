package com.example.book.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BusinessReturnDTO {
    private Integer id;
    private String name;
    private String type;
    private boolean active;
}
