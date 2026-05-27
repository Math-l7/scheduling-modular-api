package com.example.book.user.dto;

import com.example.book.user.enums.UserRoleEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserReturnDTO {

    private Integer id;
    private String name;
    private String email;
    private UserRoleEnum role;

}
