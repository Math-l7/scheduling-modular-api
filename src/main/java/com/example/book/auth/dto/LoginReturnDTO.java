package com.example.book.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginReturnDTO {
    private Integer id;
    private String name;
    private String email;
}
