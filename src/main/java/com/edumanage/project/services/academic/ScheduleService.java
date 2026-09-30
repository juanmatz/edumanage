package com.edumanage.project.services.academic;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.edumanage.project.dtos.academic.ScheduleDTO;
import com.edumanage.project.models.academic.Course;
import com.edumanage.project.models.academic.Schedule;
import com.edumanage.project.repository.academic.CourseRepository;
import com.edumanage.project.repository.academic.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<ScheduleDTO> getAllSchedulesDTO() {
        return scheduleRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    public ScheduleDTO getScheduleDTOById(Long id) {
        return convertToDTO(getScheduleById(id));
    }

    public Schedule createSchedule(Long courseId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        validate(dayOfWeek, startTime, endTime);
        Schedule schedule = new Schedule(null, findCourse(courseId), dayOfWeek, startTime, endTime);
        return scheduleRepository.save(schedule);
    }

    public ScheduleDTO createSchedule(ScheduleDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Schedule data is required");
        }
        Schedule schedule = createSchedule(dto.getCourseId(), dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
        return convertToDTO(schedule);
    }

    public Schedule updateSchedule(Long id, Long courseId, DayOfWeek dayOfWeek,
            LocalTime startTime, LocalTime endTime) {
        Schedule schedule = getScheduleById(id);
        DayOfWeek newDay = dayOfWeek != null ? dayOfWeek : schedule.getDayOfWeek();
        LocalTime newStart = startTime != null ? startTime : schedule.getStartTime();
        LocalTime newEnd = endTime != null ? endTime : schedule.getEndTime();
        validate(newDay, newStart, newEnd);
        if (courseId != null) {
            schedule.setCourse(findCourse(courseId));
        }
        schedule.setDayOfWeek(newDay);
        schedule.setStartTime(newStart);
        schedule.setEndTime(newEnd);
        return scheduleRepository.save(schedule);
    }

    public ScheduleDTO updateSchedule(Long id, ScheduleDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Schedule data is required");
        }
        Schedule schedule = updateSchedule(id, dto.getCourseId(), dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
        return convertToDTO(schedule);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.delete(getScheduleById(id));
    }

    public ScheduleDTO convertToDTO(Schedule schedule) {
        if (schedule == null) return null;
        return new ScheduleDTO(
                schedule.getId(),
                schedule.getCourse() != null ? schedule.getCourse().getId() : null,
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime()
        );
    }

    private Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
    }

    private void validate(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if (dayOfWeek == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Schedule day and times are required");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Schedule end time must be after start time");
        }
    }
}
