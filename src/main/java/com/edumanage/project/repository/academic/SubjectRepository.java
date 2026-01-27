package com.edumanage.project.repository.academic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumanage.project.models.academic.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
