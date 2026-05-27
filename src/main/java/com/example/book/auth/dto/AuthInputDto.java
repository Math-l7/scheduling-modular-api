package com.example.book.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthInputDto {

    private String name;
    private String email;
    private String password;

}
