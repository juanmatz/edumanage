package com.edumanage.project.repository.academic;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumanage.project.models.academic.performance.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent_Id(Long studentId);

    List<Attendance> findByStudent_IdAndDateBetween(Long studentId, LocalDate from, LocalDate to);

    List<Attendance> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    Optional<Attendance> findByStudent_IdAndCourse_IdAndDate(Long studentId, Long courseId, LocalDate date);

    List<Attendance> findByCourse_Id(Long courseId);
}
