package com.edumanage.project.services.academic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.edumanage.project.dtos.academic.SubjectDTO;
import com.edumanage.project.models.academic.Subject;
import com.edumanage.project.repository.academic.SubjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public List<SubjectDTO> getAllSubjectsDTO() {
        return subjectRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
    }

    public SubjectDTO getSubjectDTOById(Long id) {
        return convertToDTO(getSubjectById(id));
    }

    public Subject createSubject(String subjectName) {
        validateName(subjectName);
        return subjectRepository.save(new Subject(null, subjectName.trim()));
    }

    public SubjectDTO createSubject(SubjectDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Subject data is required");
        }
        Subject subject = createSubject(dto.getSubjectName());
        return convertToDTO(subject);
    }

    public Subject updateSubject(Long id, String subjectName) {
        validateName(subjectName);
        Subject subject = getSubjectById(id);
        subject.setSubjectName(subjectName.trim());
        return subjectRepository.save(subject);
    }

    public SubjectDTO updateSubject(Long id, SubjectDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Subject data is required");
        }
        Subject subject = updateSubject(id, dto.getSubjectName());
        return convertToDTO(subject);
    }

    public void deleteSubject(Long id) {
        subjectRepository.delete(getSubjectById(id));
    }

    public SubjectDTO convertToDTO(Subject subject) {
        if (subject == null) return null;
        return new SubjectDTO(subject.getId(), subject.getSubjectName());
    }

    private void validateName(String subjectName) {
        if (subjectName == null || subjectName.isBlank()) {
            throw new IllegalArgumentException("Subject name cannot be blank");
        }
    }
}
