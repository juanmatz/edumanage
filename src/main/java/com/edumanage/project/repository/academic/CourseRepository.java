package com.edumanage.project.repository.academic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumanage.project.models.academic.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
