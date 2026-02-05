package com.example.book.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.example.book.auth.dto.AuthInputDto;
import com.example.book.auth.dto.AuthReturnDTO;
import com.example.book.common.security.JwtService;
import com.example.book.user.dto.UserInputDTO;
import com.example.book.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwt;
    private final AuthenticationManager authenticationManager;

    public AuthReturnDTO register(AuthInputDto input) {

        UserInputDTO userInput = new UserInputDTO(input.getName(), input.getEmail(), input.getPassword());
        userService.createUser(userInput);
        String jwtToken = jwt.generateToken(input.getEmail());
        return new AuthReturnDTO(jwtToken);
    }

    public AuthReturnDTO login(AuthInputDto input) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));

        String token = jwt.generateToken(input.getEmail());
        return new AuthReturnDTO(token);

    }

}
