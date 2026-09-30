package com.edumanage.project.services.academic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.edumanage.project.dtos.academic.CourseDTO;
import com.edumanage.project.models.academic.Course;
import com.edumanage.project.models.academic.Grade;
import com.edumanage.project.models.academic.Subject;
import com.edumanage.project.models.user.Teacher;
import com.edumanage.project.repository.academic.CourseRepository;
import com.edumanage.project.repository.academic.GradeRepository;
import com.edumanage.project.repository.academic.SubjectRepository;
import com.edumanage.project.repository.user.TeacherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<CourseDTO> getAllCoursesDTO() {
        return courseRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public CourseDTO getCourseDTOById(Long id) {
        return convertToDTO(getCourseById(id));
    }

    public Course createCourse(Long teacherId, Long subjectId, Long gradeId, String academicPeriod) {
        validatePeriod(academicPeriod);
        Course course = new Course();
        course.setTeacher(findTeacher(teacherId));
        course.setSubject(findSubject(subjectId));
        course.setGrade(findGrade(gradeId));
        course.setAcademicPeriod(academicPeriod.trim());
        return courseRepository.save(course);
    }

    public CourseDTO createCourse(CourseDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Course data is required");
        }
        Course course = createCourse(dto.getTeacherId(), dto.getSubjectId(), dto.getGradeId(), dto.getAcademicPeriod());
        return convertToDTO(course);
    }

    public Course updateCourse(Long id, Long teacherId, Long subjectId, Long gradeId, String academicPeriod) {
        Course course = getCourseById(id);
        if (teacherId != null) {
            course.setTeacher(findTeacher(teacherId));
        }
        if (subjectId != null) {
            course.setSubject(findSubject(subjectId));
        }
        if (gradeId != null) {
            course.setGrade(findGrade(gradeId));
        }
        if (academicPeriod != null) {
            validatePeriod(academicPeriod);
            course.setAcademicPeriod(academicPeriod.trim());
        }
        return courseRepository.save(course);
    }

    public CourseDTO updateCourse(Long id, CourseDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Course data is required");
        }
        Course course = updateCourse(id, dto.getTeacherId(), dto.getSubjectId(), dto.getGradeId(), dto.getAcademicPeriod());
        return convertToDTO(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.delete(getCourseById(id));
    }

    public CourseDTO convertToDTO(Course course) {
        if (course == null) return null;
        return new CourseDTO(
                course.getId(),
                course.getTeacher() != null ? course.getTeacher().getId() : null,
                course.getSubject() != null ? course.getSubject().getId() : null,
                course.getGrade() != null ? course.getGrade().getId() : null,
                course.getAcademicPeriod()
        );
    }

    private Teacher findTeacher(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + id));
    }

    private Subject findSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + id));
    }

    private Grade findGrade(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found with ID: " + id));
    }

    private void validatePeriod(String academicPeriod) {
        if (academicPeriod == null || academicPeriod.isBlank()) {
            throw new IllegalArgumentException("Academic period cannot be blank");
        }
    }
}
