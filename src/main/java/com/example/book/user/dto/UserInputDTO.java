package com.example.book.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInputDTO {
    private String name;
    private String email;
    private String password;
}
