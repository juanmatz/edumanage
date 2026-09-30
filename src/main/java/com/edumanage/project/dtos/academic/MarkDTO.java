package com.edumanage.project.dtos.academic;

import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkDTO {
    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @DecimalMin(value = "0.0", message = "Mark must be at least 0")
    @DecimalMax(value = "5.0", message = "Mark must be at most 5")
    private double markValue;

    private LocalDateTime assignedAt;
}
