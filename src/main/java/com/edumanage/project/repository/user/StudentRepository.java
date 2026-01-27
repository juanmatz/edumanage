package com.edumanage.project.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumanage.project.models.user.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
