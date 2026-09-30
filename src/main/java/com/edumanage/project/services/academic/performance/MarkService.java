package com.edumanage.project.services.academic.performance;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.edumanage.project.dtos.academic.MarkDTO;
import com.edumanage.project.models.academic.Course;
import com.edumanage.project.models.academic.performance.Mark;
import com.edumanage.project.models.user.Student;
import com.edumanage.project.repository.academic.CourseRepository;
import com.edumanage.project.repository.academic.MarkRepository;
import com.edumanage.project.repository.user.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarkService {

    private static final double MIN_MARK = 0.0;
    private static final double MAX_MARK = 5.0;

    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public List<Mark> getAllMarks() {
        return markRepository.findAll();
    }

    public List<MarkDTO> getAllMarksDTO() {
        return markRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Optional<Mark> getMarkById(Long id) {
        return markRepository.findById(id);
    }

    public MarkDTO getMarkDTOById(Long id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found"));
        return convertToDTO(mark);
    }

    public List<MarkDTO> getMarksByStudent(Long studentId) {
        return markRepository.findByStudent_Id(studentId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<MarkDTO> getMarksByCourse(Long courseId) {
        return markRepository.findByCourse_Id(courseId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<MarkDTO> getMarksByStudentAndCourse(Long studentId, Long courseId) {
        return markRepository.findByStudent_IdAndCourse_Id(studentId, courseId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Mark createMark(Long studentId, Long courseId, double markValue) {
        validateMark(markValue);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        validateStudentBelongsToCourse(student, course);

        Mark mark = new Mark(null, student, course, markValue, null);
        return markRepository.save(mark);
    }

    public MarkDTO createMark(MarkDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Mark data is required");
        }
        Mark mark = createMark(dto.getStudentId(), dto.getCourseId(), dto.getMarkValue());
        return convertToDTO(mark);
    }

    public Mark updateMark(Long id, double markValue) {
        validateMark(markValue);

        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found"));
        mark.setMarkValue(markValue);
        return markRepository.save(mark);
    }

    public MarkDTO updateMark(Long id, MarkDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Mark data is required");
        }
        Mark mark = updateMark(id, dto.getMarkValue());
        return convertToDTO(mark);
    }

    public void deleteMark(Long id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found"));
        markRepository.delete(mark);
    }

    public MarkDTO convertToDTO(Mark mark) {
        if (mark == null) return null;
        return new MarkDTO(
                mark.getId(),
                mark.getStudent() != null ? mark.getStudent().getId() : null,
                mark.getCourse() != null ? mark.getCourse().getId() : null,
                mark.getMarkValue(),
                mark.getAssignedAt()
        );
    }

    public double validateMark(double markValue) {
        if (!Double.isFinite(markValue) || markValue < MIN_MARK || markValue > MAX_MARK) {
            throw new IllegalArgumentException("Mark must be between 0 and 5");
        }
        return markValue;
    }

    private void validateStudentBelongsToCourse(Student student, Course course) {
        if (student.getGrade() == null || course.getGrade() == null
                || !student.getGrade().getId().equals(course.getGrade().getId())) {
            throw new IllegalArgumentException("Student is not enrolled in this course");
        }
    }
}
