package com.example.book.user.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.book.common.exceptions.BusinessException;
import com.example.book.common.exceptions.ResourceNotFoundException;
import com.example.book.user.dto.ChangePasswordDTO;
import com.example.book.user.dto.UserInputDTO;
import com.example.book.user.dto.UserReturnDTO;
import com.example.book.user.enums.UserRoleEnum;
import com.example.book.user.model.User;
import com.example.book.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bcrypt;

    private UserReturnDTO toDTO(User user) {
        return new UserReturnDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }

    public UserReturnDTO createUser(UserInputDTO input) {
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new BusinessException("Usuário já cadastrado.");
        }

        User user = new User(input.getName(), input.getEmail(), bcrypt.encode(input.getPassword()));
        user.setRole(UserRoleEnum.CLIENT);

        userRepository.save(user);
        return toDTO(user);

    }

    public UserReturnDTO getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        return toDTO(user);

    }

    public UserReturnDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado."));

        return toDTO(user);
    }

    public List<UserReturnDTO> getUsersByRole(UserRoleEnum role) {
        List<User> users = userRepository.findByRole(role);

        return users.stream().map(u -> toDTO(u)).toList();
    }

    public UserReturnDTO changePassword(Integer userId, ChangePasswordDTO passwords) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (!bcrypt.matches(passwords.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Senha atual inválida.");
        }

        if (bcrypt.matches(passwords.getNewPassword(), user.getPassword())) {
            throw new BusinessException("A nova senha não pode ser igual à atual.");
        }

        user.setPassword(bcrypt.encode(passwords.getNewPassword()));
        userRepository.save(user);
        return toDTO(user);
    }

    public UserReturnDTO changeUserRole(Integer userId, UserRoleEnum role) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (user.getRole().equals(role)) {
            throw new BusinessException("Role já atribuída a este usuário.");
        }

        user.setRole(role);
        userRepository.save(user);
        return toDTO(user);
    }

    public void desactivateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (user.getRole().equals(UserRoleEnum.OWNER)) {
            throw new BusinessException("Um usuário Owner não pode ser desativado.");
        }

        user.setActive(false);
        userRepository.save(user);
    }

    public void activateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        user.setActive(true);
        userRepository.save(user);
    }

}
