package com.edumanage.project.repository.academic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumanage.project.models.academic.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
