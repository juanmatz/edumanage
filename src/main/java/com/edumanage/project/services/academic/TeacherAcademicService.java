package com.edumanage.project.services.academic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.edumanage.project.models.academic.Course;
import com.edumanage.project.models.academic.Schedule;
import com.edumanage.project.models.user.Student;
import com.edumanage.project.repository.academic.CourseRepository;
import com.edumanage.project.repository.academic.ScheduleRepository;
import com.edumanage.project.repository.user.StudentRepository;
import com.edumanage.project.repository.user.TeacherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherAcademicService {
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;

    public List<Course> getCourses(Long teacherId) {
        ensureTeacherExists(teacherId);
        return courseRepository.findByTeacher_Id(teacherId);
    }

    public List<Student> getStudentsByCourse(Long teacherId, Long courseId) {
        ensureTeacherOwnsCourse(teacherId, courseId);
        Course course = courseRepository.findById(courseId).orElseThrow();
        if (course.getGrade() == null) {
            return List.of();
        }
        return studentRepository.findByGrade_Id(course.getGrade().getId());
    }

    public List<Schedule> getSchedules(Long teacherId) {
        return getCourses(teacherId).stream()
                .flatMap(course -> scheduleRepository.findByCourse_Id(course.getId()).stream())
                .toList();
    }

    private void ensureTeacherExists(Long teacherId) {
        teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    private void ensureTeacherOwnsCourse(Long teacherId, Long courseId) {
        ensureTeacherExists(teacherId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (course.getTeacher() == null || !teacherId.equals(course.getTeacher().getId())) {
            throw new RuntimeException("Teacher is not assigned to this course");
        }
    }
}
