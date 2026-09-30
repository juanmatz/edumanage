package com.edumanage.project.dtos.academic;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceSummary {
    private long present;
    private long absent;
    private long excused;
    private long tardy;
    private double attendancePercentage;
}
