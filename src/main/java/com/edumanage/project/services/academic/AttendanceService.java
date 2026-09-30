package com.edumanage.project.services.academic;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.edumanage.project.dtos.academic.AttendanceDTO;
import com.edumanage.project.models.academic.Course;
import com.edumanage.project.models.academic.performance.Attendance;
import com.edumanage.project.models.enums.AttendanceStatus;
import com.edumanage.project.models.user.Student;
import com.edumanage.project.repository.academic.AttendanceRepository;
import com.edumanage.project.repository.academic.CourseRepository;
import com.edumanage.project.repository.user.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public List<AttendanceDTO> getAllAttendancesDTO() {
        return attendanceRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Optional<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }

    public AttendanceDTO getAttendanceDTOById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
        return convertToDTO(attendance);
    }

    public List<AttendanceDTO> getAttendancesByStudent(Long studentId) {
        return attendanceRepository.findByStudent_Id(studentId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<AttendanceDTO> getAttendancesByCourse(Long courseId) {
        return attendanceRepository.findByCourse_Id(courseId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Attendance createAttendance(Long studentId, Long courseId, LocalDate date,
            AttendanceStatus status) {
        validateAttendanceDate(date);
        validateAttendanceStatus(status);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        validateStudentBelongsToCourse(student, course);

        Optional<Attendance> existing = attendanceRepository
                .findByStudent_IdAndCourse_IdAndDate(studentId, courseId, date);
        if (existing.isPresent()) {
            Attendance attendance = existing.get();
            attendance.setStatus(status);
            return attendanceRepository.save(attendance);
        }

        Attendance attendance = new Attendance(null, student, course, date, status);
        return attendanceRepository.save(attendance);
    }

    public AttendanceDTO createAttendance(AttendanceDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Attendance data is required");
        }
        Attendance attendance = createAttendance(dto.getStudentId(), dto.getCourseId(), dto.getDate(), dto.getStatus());
        return convertToDTO(attendance);
    }

    public Attendance updateAttendance(Long id, LocalDate date, AttendanceStatus status) {
        validateAttendanceDate(date);
        validateAttendanceStatus(status);

        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
        attendance.setDate(date);
        attendance.setStatus(status);
        return attendanceRepository.save(attendance);
    }

    public AttendanceDTO updateAttendance(Long id, AttendanceDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Attendance data is required");
        }
        Attendance attendance = updateAttendance(id, dto.getDate(), dto.getStatus());
        return convertToDTO(attendance);
    }

    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
        attendanceRepository.delete(attendance);
    }

    public AttendanceDTO convertToDTO(Attendance attendance) {
        if (attendance == null) return null;
        return new AttendanceDTO(
                attendance.getId(),
                attendance.getStudent() != null ? attendance.getStudent().getId() : null,
                attendance.getCourse() != null ? attendance.getCourse().getId() : null,
                attendance.getDate(),
                attendance.getStatus()
        );
    }

    public boolean isValidAttendanceDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }

    public void validateAttendanceDate(LocalDate date) {
        if (!isValidAttendanceDate(date)) {
            throw new IllegalArgumentException("Attendance date cannot be null or in the future");
        }
    }

    public void validateAttendanceStatus(AttendanceStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Attendance status cannot be null");
        }
    }

    private void validateStudentBelongsToCourse(Student student, Course course) {
        if (student.getGrade() == null || course.getGrade() == null
                || !student.getGrade().getId().equals(course.getGrade().getId())) {
            throw new IllegalArgumentException("Student is not enrolled in this course");
        }
    }
}
