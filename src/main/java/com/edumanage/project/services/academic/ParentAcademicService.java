package com.edumanage.project.services.academic;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.edumanage.project.dtos.academic.AttendanceSummary;
import com.edumanage.project.models.academic.Schedule;
import com.edumanage.project.models.academic.performance.Attendance;
import com.edumanage.project.models.academic.performance.Mark;
import com.edumanage.project.models.enums.AttendanceStatus;
import com.edumanage.project.models.user.Parent;
import com.edumanage.project.repository.academic.AttendanceRepository;
import com.edumanage.project.repository.academic.CourseRepository;
import com.edumanage.project.repository.academic.MarkRepository;
import com.edumanage.project.repository.academic.ScheduleRepository;
import com.edumanage.project.repository.user.ParentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParentAcademicService {
    private final ParentRepository parentRepository;
    private final MarkRepository markRepository;
    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;

    public List<Mark> getChildMarks(Long parentId, Long studentId) {
        ensureLinked(parentId, studentId);
        return markRepository.findByStudent_Id(studentId);
    }

    public List<Attendance> getChildAttendances(Long parentId, Long studentId,
            LocalDate from, LocalDate to) {
        ensureLinked(parentId, studentId);
        validateRange(from, to);
        return attendanceRepository.findByStudent_IdAndDateBetween(studentId, from, to);
    }

    public AttendanceSummary getChildAttendanceSummary(Long parentId, Long studentId,
            LocalDate from, LocalDate to) {
        List<Attendance> attendances = getChildAttendances(parentId, studentId, from, to);
        long present = count(attendances, AttendanceStatus.PRESENT);
        long absent = count(attendances, AttendanceStatus.ABSENT);
        long excused = count(attendances, AttendanceStatus.EXCUSED);
        long tardy = count(attendances, AttendanceStatus.TARDY);
        long counted = present + absent + tardy;
        double percentage = counted == 0 ? 0.0 : (present * 100.0) / counted;
        return new AttendanceSummary(present, absent, excused, tardy, percentage);
    }

    public List<Schedule> getChildSchedules(Long parentId, Long studentId) {
        Parent parent = ensureLinked(parentId, studentId);
        return parent.getStudents().stream()
                .filter(student -> studentId.equals(student.getId()))
                .findFirst()
                .filter(student -> student.getGrade() != null)
                .map(student -> courseRepository.findByGrade_Id(student.getGrade().getId()).stream()
                        .flatMap(course -> scheduleRepository.findByCourse_Id(course.getId()).stream())
                        .toList())
                .orElse(List.of());
    }

    private Parent ensureLinked(Long parentId, Long studentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
        boolean linked = parent.getStudents() != null && parent.getStudents().stream()
                .anyMatch(student -> studentId.equals(student.getId()));
        if (!linked) {
            throw new RuntimeException("Student is not linked to this parent");
        }
        return parent;
    }

    private long count(List<Attendance> attendances, AttendanceStatus status) {
        return attendances.stream().filter(attendance -> attendance.getStatus() == status).count();
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null || to.isBefore(from)) {
            throw new IllegalArgumentException("Invalid attendance date range");
        }
    }
}
