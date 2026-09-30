package com.edumanage.project.controllers.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edumanage.project.dtos.academic.CourseDTO;
import com.edumanage.project.dtos.academic.MarkDTO;
import com.edumanage.project.dtos.academic.ScheduleDTO;
import com.edumanage.project.dtos.academic.AttendanceDTO;
import com.edumanage.project.dtos.user.StudentDTO;
import com.edumanage.project.services.academic.CourseService;
import com.edumanage.project.services.academic.ScheduleService;
import com.edumanage.project.services.academic.StudentAcademicService;
import com.edumanage.project.services.academic.AttendanceService;
import com.edumanage.project.services.academic.performance.MarkService;
import com.edumanage.project.services.user.StudentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentAcademicService studentAcademicService;
    private final CourseService courseService;
    private final MarkService markService;
    private final AttendanceService attendanceService;
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO created = studentService.createStudent(studentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        StudentDTO updated = studentService.updateStudent(id, studentDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseDTO>> getStudentCourses(@PathVariable Long id) {
        List<CourseDTO> courses = studentAcademicService.getCourses(id).stream()
                .map(courseService::convertToDTO)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}/marks")
    public ResponseEntity<List<MarkDTO>> getStudentMarks(
            @PathVariable Long id,
            @RequestParam(required = false) Long courseId) {
        List<MarkDTO> marks;
        if (courseId != null) {
            marks = studentAcademicService.getMarks(id, courseId).stream()
                    .map(markService::convertToDTO)
                    .toList();
        } else {
            marks = studentAcademicService.getMarks(id).stream()
                    .map(markService::convertToDTO)
                    .toList();
        }
        return ResponseEntity.ok(marks);
    }

    @GetMapping("/{id}/attendances")
    public ResponseEntity<List<AttendanceDTO>> getStudentAttendances(@PathVariable Long id) {
        List<AttendanceDTO> attendances = studentAcademicService.getAttendances(id).stream()
                .map(attendanceService::convertToDTO)
                .toList();
        return ResponseEntity.ok(attendances);
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<ScheduleDTO>> getStudentSchedules(@PathVariable Long id) {
        List<ScheduleDTO> schedules = studentAcademicService.getSchedules(id).stream()
                .map(scheduleService::convertToDTO)
                .toList();
        return ResponseEntity.ok(schedules);
    }
}
