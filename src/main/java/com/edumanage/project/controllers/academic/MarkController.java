package com.edumanage.project.controllers.academic;

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

import com.edumanage.project.dtos.academic.MarkDTO;
import com.edumanage.project.services.academic.performance.MarkService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/marks")
@RequiredArgsConstructor
public class MarkController {

    private final MarkService markService;

    @GetMapping
    public ResponseEntity<List<MarkDTO>> getAllMarks(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId) {
        if (studentId != null && courseId != null) {
            return ResponseEntity.ok(markService.getMarksByStudentAndCourse(studentId, courseId));
        }
        if (studentId != null) {
            return ResponseEntity.ok(markService.getMarksByStudent(studentId));
        }
        if (courseId != null) {
            return ResponseEntity.ok(markService.getMarksByCourse(courseId));
        }
        return ResponseEntity.ok(markService.getAllMarksDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkDTO> getMarkById(@PathVariable Long id) {
        return ResponseEntity.ok(markService.getMarkDTOById(id));
    }

    @PostMapping
    public ResponseEntity<MarkDTO> createMark(@Valid @RequestBody MarkDTO markDTO) {
        MarkDTO created = markService.createMark(markDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkDTO> updateMark(@PathVariable Long id, @RequestBody MarkDTO markDTO) {
        MarkDTO updated = markService.updateMark(id, markDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMark(@PathVariable Long id) {
        markService.deleteMark(id);
        return ResponseEntity.noContent().build();
    }
}
