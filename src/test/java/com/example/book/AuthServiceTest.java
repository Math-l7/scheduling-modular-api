package com.example.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;

import com.example.book.auth.dto.AuthInputDto;
import com.example.book.auth.dto.AuthReturnDTO;
import com.example.book.auth.service.AuthService;
import com.example.book.common.security.JwtService;
import com.example.book.user.dto.UserInputDTO;
import com.example.book.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private JwtService jwt;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private AuthInputDto authInputDto;
    private UserInputDTO userInputDTO;

    @BeforeEach
    void setUp() {
        authInputDto = new AuthInputDto("Matheus", "matheus.com", "senha123");
        userInputDTO = new UserInputDTO(authInputDto.getName(), authInputDto.getEmail(), authInputDto.getPassword());
        lenient().when(jwt.generateToken(userInputDTO.getEmail())).thenReturn("tokenJwt");
    }

    @Test
    public void registerTest_Success() {
        AuthReturnDTO authReturn = authService.register(authInputDto);

        assertEquals("tokenJwt", authReturn.getToken());
        verify(userService).createUser(any(UserInputDTO.class));

    }

    @Test
    public void loginTest_Success() {
        AuthReturnDTO loginReturn = authService.login(authInputDto);

        assertNotNull(loginReturn);
        assertEquals("tokenJwt", loginReturn.getToken());

        verify(authenticationManager).authenticate(any());
    }

    @Test
    public void login_WhenPasswordInvalid() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Erro"));

        assertThrows(BadCredentialsException.class, () -> authService.login(authInputDto));

        verify(jwt, never()).generateToken(anyString());
    }

}
