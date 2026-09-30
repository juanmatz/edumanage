package com.edumanage.project.dtos.academic;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private Long id;

    @NotBlank(message = "Subject name is required")
    private String subjectName;
}
