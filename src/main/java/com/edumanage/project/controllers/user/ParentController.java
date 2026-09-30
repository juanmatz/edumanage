package com.edumanage.project.controllers.user;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.edumanage.project.dtos.academic.AttendanceDTO;
import com.edumanage.project.dtos.academic.AttendanceSummary;
import com.edumanage.project.dtos.academic.MarkDTO;
import com.edumanage.project.dtos.academic.ScheduleDTO;
import com.edumanage.project.dtos.user.ParentDTO;
import com.edumanage.project.services.academic.AttendanceService;
import com.edumanage.project.services.academic.ParentAcademicService;
import com.edumanage.project.services.academic.ScheduleService;
import com.edumanage.project.services.academic.performance.MarkService;
import com.edumanage.project.services.user.ParentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;
    private final ParentAcademicService parentAcademicService;
    private final MarkService markService;
    private final AttendanceService attendanceService;
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ParentDTO>> getAllParents() {
        return ResponseEntity.ok(parentService.getAllParentsDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParentDTO> getParentById(@PathVariable Long id) {
        return ResponseEntity.ok(parentService.getParentDTOById(id));
    }

    @PostMapping
    public ResponseEntity<ParentDTO> createParent(@Valid @RequestBody ParentDTO parentDTO) {
        ParentDTO created = parentService.createParent(parentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParentDTO> updateParent(@PathVariable Long id, @RequestBody ParentDTO parentDTO) {
        ParentDTO updated = parentService.updateParent(id, parentDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/students/{studentId}")
    public ResponseEntity<ParentDTO> linkStudent(
            @PathVariable Long id,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(parentService.linkStudentDTO(id, studentId));
    }

    @DeleteMapping("/{id}/students/{studentId}")
    public ResponseEntity<ParentDTO> unlinkStudent(
            @PathVariable Long id,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(parentService.unlinkStudentDTO(id, studentId));
    }

    @GetMapping("/{id}/children/{studentId}/marks")
    public ResponseEntity<List<MarkDTO>> getChildMarks(
            @PathVariable Long id,
            @PathVariable Long studentId) {
        List<MarkDTO> marks = parentAcademicService.getChildMarks(id, studentId).stream()
                .map(markService::convertToDTO)
                .toList();
        return ResponseEntity.ok(marks);
    }

    @GetMapping("/{id}/children/{studentId}/attendances")
    public ResponseEntity<List<AttendanceDTO>> getChildAttendances(
            @PathVariable Long id,
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<AttendanceDTO> attendances = parentAcademicService.getChildAttendances(id, studentId, from, to).stream()
                .map(attendanceService::convertToDTO)
                .toList();
        return ResponseEntity.ok(attendances);
    }

    @GetMapping("/{id}/children/{studentId}/attendances/summary")
    public ResponseEntity<AttendanceSummary> getChildAttendanceSummary(
            @PathVariable Long id,
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        AttendanceSummary summary = parentAcademicService.getChildAttendanceSummary(id, studentId, from, to);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{id}/children/{studentId}/schedules")
    public ResponseEntity<List<ScheduleDTO>> getChildSchedules(
            @PathVariable Long id,
            @PathVariable Long studentId) {
        List<ScheduleDTO> schedules = parentAcademicService.getChildSchedules(id, studentId).stream()
                .map(scheduleService::convertToDTO)
                .toList();
        return ResponseEntity.ok(schedules);
    }
}
