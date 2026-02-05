package com.example.book.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StaffInputDTO {
    private Integer userId;
    private String publicName;
    private Integer businessId;
}
