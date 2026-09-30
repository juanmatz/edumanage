package com.edumanage.project.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumanage.project.models.user.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByGrade_Id(Long gradeId);
}
