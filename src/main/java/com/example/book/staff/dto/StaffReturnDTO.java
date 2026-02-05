package com.example.book.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StaffReturnDTO {
    private Integer id;
    private String publicName;
    private boolean active;
    private Integer businessId;
}
