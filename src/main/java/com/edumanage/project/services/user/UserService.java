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

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
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
        }
        return userDTO;
    }

}
