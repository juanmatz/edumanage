package com.edumanage.project.dtos.user;

import com.edumanage.project.models.enums.RoleList;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id;

    @NotNull(message = "Role is required")
    private RoleList rol;
}
