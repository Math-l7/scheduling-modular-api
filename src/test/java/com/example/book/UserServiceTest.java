package com.example.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.book.common.exceptions.BusinessException;
import com.example.book.common.exceptions.ResourceNotFoundException;
import com.example.book.user.dto.ChangePasswordDTO;
import com.example.book.user.dto.UserInputDTO;
import com.example.book.user.dto.UserReturnDTO;
import com.example.book.user.enums.UserRoleEnum;
import com.example.book.user.model.User;
import com.example.book.user.repository.UserRepository;
import com.example.book.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder bcrypt;

    @InjectMocks
    private UserService userService;

    private User commonUser;
    private UserInputDTO userInputDTO;
    private ChangePasswordDTO changePasswordDTO;

    @BeforeEach
    void setUp() {
        commonUser = new User("matheus", "matheus@barba.com", "encoded_password");
        commonUser.setId(1);
        commonUser.setRole(UserRoleEnum.CLIENT);
        commonUser.setActive(true);

        userInputDTO = new UserInputDTO("matheus Barbeiro", "matheus@barba.com", "raw_password");

        changePasswordDTO = new ChangePasswordDTO("raw_password", "new_secure_password");

    }

    @Test
    public void createUserTest_Success() {
        when(userRepository.existsByEmail(userInputDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(commonUser);

        UserReturnDTO userReturn = userService.createUser(userInputDTO);

        assertNotNull(userReturn);
        assertEquals(userInputDTO.getEmail(), userReturn.getEmail());
        assertEquals(UserRoleEnum.CLIENT, userReturn.getRole());

        verify(userRepository).save(commonUser);
    }

    @Test
    public void createUserTest_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(userInputDTO.getEmail())).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.createUser(userInputDTO));
    }

    @Test
    public void getUserByIdTest_Success() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));

        UserReturnDTO userReturn = userService.getUserById(commonUser.getId());

        assertEquals(commonUser.getEmail(), userReturn.getEmail());
        assertEquals(commonUser.getName(), userReturn.getName());
        assertEquals(UserRoleEnum.CLIENT, userReturn.getRole());

    }

    @Test
    public void getUserByIdTest_WhenUserNotFound() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(commonUser.getId()));
    }

    @Test
    public void getUserByEmailTest_Success() {
        when(userRepository.findByEmail(commonUser.getEmail())).thenReturn(Optional.of(commonUser));

        UserReturnDTO userReturn = userService.getUserByEmail(commonUser.getEmail());

        assertEquals(commonUser.getEmail(), userReturn.getEmail());
        assertEquals(commonUser.getName(), userReturn.getName());
        assertEquals(UserRoleEnum.CLIENT, userReturn.getRole());
    }

    @Test
    public void getUserByEmailTest_WhenEmailNotFound() {
        when(userRepository.findByEmail(commonUser.getEmail())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail(commonUser.getEmail()));
    }

    @Test
    public void getUserByRole_Success() {
        when(userRepository.findByRole(UserRoleEnum.CLIENT)).thenReturn(List.of(commonUser));

        List<UserReturnDTO> listReturn = userService.getUsersByRole(UserRoleEnum.CLIENT);

        assertEquals(1, listReturn.size());
        assertEquals(commonUser.getEmail(), listReturn.get(0).getEmail());

    }

    @Test
    public void changePassword_Success() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));
        when(bcrypt.matches(anyString(), anyString())).thenReturn(true);
        when(bcrypt.matches(changePasswordDTO.getNewPassword(), "encoded_password")).thenReturn(false);

        userService.changePassword(commonUser.getId(), changePasswordDTO);

        assertEquals("encoded_password", commonUser.getPassword());
        verify(userRepository).save(commonUser);
    }

    @Test
    public void changePasswordTest_WhenUserNotFound() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.changePassword(commonUser.getId(),
                changePasswordDTO));
    }

    @Test
    public void changePasswordTest_WhenPasswordsDontMatches() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));
        when(bcrypt.matches(changePasswordDTO.getOldPassword(), commonUser.getPassword())).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.changePassword(commonUser.getId(), changePasswordDTO));
    }

    @Test
    public void changePasswordTest_WhenPasswordsIsTheSame() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));
        when(bcrypt.matches(changePasswordDTO.getOldPassword(), commonUser.getPassword())).thenReturn(true);
        when(bcrypt.matches(changePasswordDTO.getNewPassword(), commonUser.getPassword())).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.changePassword(commonUser.getId(), changePasswordDTO));
    }

    @Test
    public void changeUserRoleTest_Success() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));

        UserReturnDTO userReturn = userService.changeUserRole(commonUser.getId(), UserRoleEnum.STAFF);

        assertEquals(UserRoleEnum.STAFF, userReturn.getRole());

        verify(userRepository).save(commonUser);
    }

    @Test
    public void changeUserRoleTest_WhenUserNotFound() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.changeUserRole(commonUser.getId(), UserRoleEnum.STAFF));
    }

    @Test
    public void changeUSerRoleTest_WhenRoleAlreadyAssigned() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));

        assertThrows(BusinessException.class,
                () -> userService.changeUserRole(commonUser.getId(), UserRoleEnum.CLIENT));
    }

    @Test
    public void desactivateUserTest_Success() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));

        userService.desactivateUser(commonUser.getId());

        assertFalse(commonUser.isActive());
        verify(userRepository).save(commonUser);
    }

    @Test
    public void desactivateUserTest_WhenUserNotFound() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.desactivateUser(commonUser.getId()));
    }

    @Test
    public void desactivateUserTest_WhenUserIsOwner() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));
        commonUser.setRole(UserRoleEnum.OWNER);

        assertThrows(BusinessException.class,
                () -> userService.desactivateUser(commonUser.getId()));
    }

    @Test
    public void activateUserTest_Success() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.of(commonUser));

        userService.activateUser(commonUser.getId());

        assertTrue(commonUser.isActive());
        verify(userRepository).save(commonUser);
    }

    @Test
    public void activateUSerTest_WhenUserNotFound() {
        when(userRepository.findById(commonUser.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> userService.activateUser(commonUser.getId()));
    }

}
