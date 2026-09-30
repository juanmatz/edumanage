package com.edumanage.project.services.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

import com.edumanage.project.dtos.user.UserDTO;
import com.edumanage.project.models.enums.RoleList;
import com.edumanage.project.models.enums.UserStatus;
import com.edumanage.project.models.user.Role;
import com.edumanage.project.models.user.User;
import com.edumanage.project.repository.user.RoleRepository;
import com.edumanage.project.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private Role studentRole;
    private User user;

    @BeforeEach
    void setUp() {
        studentRole = new Role(1L, RoleList.STUDENT);
        user = new User(1L, "ana", "old-password", "ana@example.com", studentRole, UserStatus.ACTIVE);
    }

    @Test
    void getAllUsersReturnsUsersFromRepository() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
        verify(userRepository).findAll();
    }

    @Test
    void getUserByIdReturnsUserFromRepository() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertEquals(Optional.of(user), result);
    }

    @Test
    void saveUserCreatesAnActiveUserWithItsRole() {
        UserDTO userDTO = new UserDTO("ana", "password123", "ana@example.com", "student", null);
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(roleRepository.findByRol(RoleList.STUDENT)).thenReturn(Optional.of(studentRole));

        UserDTO result = userService.saveUser(userDTO);

        assertEquals(userDTO, result);
        verify(userRepository).save(any(User.class));
        verify(roleRepository).findByRol(RoleList.STUDENT);
    }

    @Test
    void saveUserRejectsDuplicatedEmail() {
        UserDTO userDTO = new UserDTO("ana", "password123", "ana@example.com", "student", null);
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.saveUser(userDTO));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserChangesOnlyProvidedFields() {
        UserDTO userDTO = new UserDTO(null, null, "new@example.com", null, "inactive");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.updateUser(1L, userDTO);

        assertEquals(userDTO, result);
        assertEquals("new@example.com", user.getEmail());
        assertEquals(UserStatus.INACTIVE, user.getStatus());
        assertEquals(studentRole, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUserMarksUserAsInactive() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        assertEquals(UserStatus.INACTIVE, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordUpdatesPasswordWhenCurrentPasswordIsCorrect() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.changePassword(1L, "old-password", "new-password");

        assertEquals("new-password", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordRejectsIncorrectCurrentPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.changePassword(1L, "wrong-password", "new-password"));

        assertEquals("Incorrect current password", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}