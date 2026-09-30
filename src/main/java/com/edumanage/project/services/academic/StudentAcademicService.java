package com.edumanage.project.services.academic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.edumanage.project.models.academic.Course;
import com.edumanage.project.models.academic.Schedule;
import com.edumanage.project.models.academic.performance.Attendance;
import com.edumanage.project.models.academic.performance.Mark;
import com.edumanage.project.models.user.Student;
import com.edumanage.project.repository.academic.AttendanceRepository;
import com.edumanage.project.repository.academic.CourseRepository;
import com.edumanage.project.repository.academic.MarkRepository;
import com.edumanage.project.repository.academic.ScheduleRepository;
import com.edumanage.project.repository.user.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentAcademicService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final MarkRepository markRepository;
    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;

    public List<Course> getCourses(Long studentId) {
        Student student = getStudent(studentId);
        if (student.getGrade() == null) {
            return List.of();
        }
        return courseRepository.findByGrade_Id(student.getGrade().getId());
    }

    public List<Mark> getMarks(Long studentId) {
        getStudent(studentId);
        return markRepository.findByStudent_Id(studentId);
    }

    public List<Mark> getMarks(Long studentId, Long courseId) {
        getStudent(studentId);
        return markRepository.findByStudent_IdAndCourse_Id(studentId, courseId);
    }

    public List<Attendance> getAttendances(Long studentId) {
        getStudent(studentId);
        return attendanceRepository.findByStudent_Id(studentId);
    }

    public List<Schedule> getSchedules(Long studentId) {
        return getCourses(studentId).stream()
                .flatMap(course -> scheduleRepository.findByCourse_Id(course.getId()).stream())
                .toList();
    }

    private Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
}
