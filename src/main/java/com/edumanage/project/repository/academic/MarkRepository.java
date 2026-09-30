package com.edumanage.project.repository.academic;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumanage.project.models.academic.performance.Mark;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    List<Mark> findByStudent_Id(Long studentId);

    List<Mark> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    List<Mark> findByCourse_Id(Long courseId);
}
