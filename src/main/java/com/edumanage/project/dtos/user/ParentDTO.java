package com.edumanage.project.dtos.user;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentDTO {
    private Long id;

    @NotBlank(message = "Username is required")
    private String userName;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Email(message = "Must be a valid email address")
    private String email;

    @Valid
    private PersonDTO person;

    private List<Long> studentIds;
}
