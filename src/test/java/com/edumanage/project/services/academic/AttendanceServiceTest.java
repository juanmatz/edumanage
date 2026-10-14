package com.edumanage.project.services.academic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.edumanage.project.models.enums.AttendanceStatus;

class AttendanceServiceTest {

    private final AttendanceService attendanceService = new AttendanceService(null, null, null);

    @Test
    void acceptsPastAttendanceDate() {
        assertFalse(attendanceService.isValidAttendanceDate(LocalDate.now().minusDays(1)));
    }

    @Test
    void acceptsCurrentAttendanceDate() {
        assertTrue(attendanceService.isValidAttendanceDate(LocalDate.now()));
    }

    @Test
    void rejectsFutureAttendanceDate() {
        assertFalse(attendanceService.isValidAttendanceDate(LocalDate.now().plusDays(1)));
    }

    @Test
    void rejectsNullAttendanceDate() {
        assertFalse(attendanceService.isValidAttendanceDate(null));
    }

    @Test
    void validateAttendanceDateRejectsFutureDate() {
        assertThrows(IllegalArgumentException.class,
                () -> attendanceService.validateAttendanceDate(LocalDate.now().plusDays(1)));
    }

    @Test
    void acceptsAttendanceStatus() {
        assertDoesNotThrow(() -> attendanceService.validateAttendanceStatus(AttendanceStatus.PRESENT));
    }

    @Test
    void rejectsNullAttendanceStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> attendanceService.validateAttendanceStatus(null));
    }
}