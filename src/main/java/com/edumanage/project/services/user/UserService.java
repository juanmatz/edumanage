package com.edumanage.project.services.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.edumanage.project.dtos.user.UserDTO;
import com.edumanage.project.models.enums.RoleList;
import com.edumanage.project.models.enums.UserStatus;
import com.edumanage.project.models.user.Role;
import com.edumanage.project.models.user.User;
import com.edumanage.project.repository.user.RoleRepository;
import com.edumanage.project.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserDTO> getAllUsersDTO() {
        return userRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public UserDTO getUserDTOById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not found"));
        return convertToDTO(user);
    }

    public UserDTO saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        } else {
            User user = new User();
            user.setUserName(userDTO.getUserName());
            user.setEmail(userDTO.getEmail());  
            user.setPassword(userDTO.getPassword());
            user.setStatus(UserStatus.ACTIVE);
            try {
                RoleList roleEnum = RoleList.valueOf(userDTO.getRole().toUpperCase());
                Role role = roleRepository.findByRol(roleEnum)
                        .orElseThrow(() -> new RuntimeException("Role not found in database"));
                user.setRole(role);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role provided: " + userDTO.getRole());
            }
            userRepository.save(user);
            userDTO.setId(user.getId());
        }
        return userDTO;
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));

        if (userDTO.getEmail() != null) {
            if (!userDTO.getEmail().equals(user.getEmail())
                    && userRepository.existsByEmail(userDTO.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getRole() != null) {
            try {
                RoleList roleEnum = RoleList.valueOf(userDTO.getRole().toUpperCase());
                Role role = roleRepository.findByRol(roleEnum)
                        .orElseThrow(() -> new RuntimeException("Role not found in database"));
                user.setRole(role);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role provided: " + userDTO.getRole());
            }
        }

        if (userDTO.getStatus() != null) {
            try {
                UserStatus status = UserStatus.valueOf(userDTO.getStatus().toUpperCase());
                user.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid Status: " + userDTO.getStatus());
            }
        }

        userRepository.save(user);
        userDTO.setId(user.getId());
        return userDTO;
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    public void changePassword(Long id, String password, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Incorrect current password");
        }
        if (password.equals(newPassword)) {
            throw new RuntimeException("New password must be different.");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null && user.getRole().getRol() != null ? user.getRole().getRol().name() : null);
        dto.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        return dto;
    }
}
