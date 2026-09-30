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
import org.springframework.web.bind.annotation.RestController;

import com.edumanage.project.dtos.academic.CourseDTO;
import com.edumanage.project.dtos.academic.ScheduleDTO;
import com.edumanage.project.dtos.user.StudentDTO;
import com.edumanage.project.dtos.user.TeacherDTO;
import com.edumanage.project.services.academic.CourseService;
import com.edumanage.project.services.academic.ScheduleService;
import com.edumanage.project.services.academic.TeacherAcademicService;
import com.edumanage.project.services.user.StudentService;
import com.edumanage.project.services.user.TeacherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final TeacherAcademicService teacherAcademicService;
    private final CourseService courseService;
    private final StudentService studentService;
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @PostMapping
    public ResponseEntity<TeacherDTO> createTeacher(@Valid @RequestBody TeacherDTO teacherDTO) {
        TeacherDTO created = teacherService.createTeacher(teacherDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO teacherDTO) {
        TeacherDTO updated = teacherService.updateTeacher(id, teacherDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseDTO>> getTeacherCourses(@PathVariable Long id) {
        List<CourseDTO> courses = teacherAcademicService.getCourses(id).stream()
                .map(courseService::convertToDTO)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}/courses/{courseId}/students")
    public ResponseEntity<List<StudentDTO>> getStudentsByCourse(
            @PathVariable Long id,
            @PathVariable Long courseId) {
        List<StudentDTO> students = teacherAcademicService.getStudentsByCourse(id, courseId).stream()
                .map(student -> studentService.getStudentById(student.getId()))
                .toList();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<ScheduleDTO>> getTeacherSchedules(@PathVariable Long id) {
        List<ScheduleDTO> schedules = teacherAcademicService.getSchedules(id).stream()
                .map(scheduleService::convertToDTO)
                .toList();
        return ResponseEntity.ok(schedules);
    }
}
