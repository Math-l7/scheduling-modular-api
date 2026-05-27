package com.example.book.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.common.exceptions.BusinessException;
import com.example.book.common.security.LoggedUserService;
import com.example.book.user.dto.ChangePasswordDTO;
import com.example.book.user.dto.UserInputDTO;
import com.example.book.user.dto.UserReturnDTO;
import com.example.book.user.enums.UserRoleEnum;
import com.example.book.user.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LoggedUserService loggedUserService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserReturnDTO createUser(@RequestBody UserInputDTO input) {
        return userService.createUser(input);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.OK)
    public UserReturnDTO getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.OK)
    public UserReturnDTO getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserReturnDTO getMe() {
        return userService.getUserByEmail(loggedUserService.get().getEmail());
    }

    @GetMapping("/role")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.OK)
    public List<UserReturnDTO> getUserByRole(@RequestParam String role) {
        UserRoleEnum roleEnum;
        try {
            roleEnum = UserRoleEnum.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new BusinessException("Role inválida.");
        }

        return userService.getUsersByRole(roleEnum);
    }

    @PutMapping("/me/password")
    public UserReturnDTO changePassword(@RequestBody ChangePasswordDTO passwords) {
        return userService.changePassword(loggedUserService.get().getId(), passwords);
    }

    @PutMapping("/change-role/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.OK)
    public UserReturnDTO changeUserRole(@PathVariable Integer id, @RequestParam String role) {
        UserRoleEnum roleEnum;
        try {
            roleEnum = UserRoleEnum.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new BusinessException("Role inválida.");

        }
        return userService.changeUserRole(id, roleEnum);
    }

    @PutMapping("/me/desactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivateMe() {
        userService.desactivateUser(loggedUserService.get().getId());
    }

    @PutMapping("/me/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateMe() {
        userService.activateUser(loggedUserService.get().getId());
    }

    @PutMapping("/{id}/desactivate")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivateUser(@PathVariable Integer id) {
        userService.desactivateUser(id);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateUser(@PathVariable Integer id) {
        userService.activateUser(id);
    }

}
