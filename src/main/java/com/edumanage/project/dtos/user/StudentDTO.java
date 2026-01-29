package com.edumanage.project.dtos.user;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    @NotBlank(message = "Username is Required")
    private String username;
    @Size(min = 8, message = "password must be at least 8 characters long")
    private String password;
    @Email(message = "must be a valid email address")
    private String email;

    @NotBlank(message = "FirstName is Required")
    private String firstName;
    private String middleName;
    @NotBlank(message = "LastName is Required")
    private String lastName;
    @NotBlank(message = "DNI is Required")
    @Size(min = 7, max = 15, message = "Invalid DNI format")
    private String dni;
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    private String phone;
    private String address;

    @NotNull(message = "Grade ID is required")
    private Long gradeId;
}
