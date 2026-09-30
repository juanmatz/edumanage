package com.edumanage.project.services.academic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.edumanage.project.dtos.academic.GradeDTO;
import com.edumanage.project.models.academic.Grade;
import com.edumanage.project.repository.academic.GradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public List<GradeDTO> getAllGradesDTO() {
        return gradeRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Grade getGradeById(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
    }

    public GradeDTO getGradeDTOById(Long id) {
        return convertToDTO(getGradeById(id));
    }

    public Grade createGrade(String gradeName, int maxCapacity) {
        validate(gradeName, maxCapacity);
        return gradeRepository.save(new Grade(null, gradeName.trim(), maxCapacity));
    }

    public GradeDTO createGrade(GradeDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Grade data is required");
        }
        Grade grade = createGrade(dto.getGradeName(), dto.getMaxCapacity() != null ? dto.getMaxCapacity() : 0);
        return convertToDTO(grade);
    }

    public Grade updateGrade(Long id, String gradeName, Integer maxCapacity) {
        Grade grade = getGradeById(id);
        if (gradeName != null) {
            validateName(gradeName);
            grade.setGradeName(gradeName.trim());
        }
        if (maxCapacity != null) {
            validateCapacity(maxCapacity);
            grade.setMax_capacity(maxCapacity);
        }
        return gradeRepository.save(grade);
    }

    public GradeDTO updateGrade(Long id, GradeDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Grade data is required");
        }
        Grade grade = updateGrade(id, dto.getGradeName(), dto.getMaxCapacity());
        return convertToDTO(grade);
    }

    public void deleteGrade(Long id) {
        gradeRepository.delete(getGradeById(id));
    }

    public GradeDTO convertToDTO(Grade grade) {
        if (grade == null) return null;
        return new GradeDTO(grade.getId(), grade.getGradeName(), grade.getMax_capacity());
    }

    private void validate(String gradeName, int maxCapacity) {
        validateName(gradeName);
        validateCapacity(maxCapacity);
    }

    private void validateName(String gradeName) {
        if (gradeName == null || gradeName.isBlank()) {
            throw new IllegalArgumentException("Grade name cannot be blank");
        }
    }

    private void validateCapacity(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Grade capacity must be greater than zero");
        }
    }
}
