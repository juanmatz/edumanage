package com.edumanage.project.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;

    @NotBlank(message = "username is required")
    private String userName;

    @Size(min = 8, message = "password must be at least 8 characters long")
    private String password;

    @Email(message = "must be a valid email address")
    private String email;

    private String role;
    private String status;

    public UserDTO(String userName, String password, String email, String role, String status) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.role = role;
        this.status = status;
    }
}
