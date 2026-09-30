package com.edumanage.project.controllers.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.edumanage.project.dtos.academic.AttendanceSummary;
import com.edumanage.project.dtos.user.ParentDTO;
import com.edumanage.project.dtos.user.PersonDTO;
import com.edumanage.project.exceptions.GlobalExceptionHandler;
import com.edumanage.project.services.academic.AttendanceService;
import com.edumanage.project.services.academic.ParentAcademicService;
import com.edumanage.project.services.academic.ScheduleService;
import com.edumanage.project.services.academic.performance.MarkService;
import com.edumanage.project.services.user.ParentService;

@ExtendWith(MockitoExtension.class)
class ParentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ParentService parentService;

    @Mock
    private ParentAcademicService parentAcademicService;

    @Mock
    private MarkService markService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private ParentController parentController;

    private ParentDTO parentDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(parentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        PersonDTO person = new PersonDTO(1L, "Marta", null, "Gomez", "87654321",
                LocalDate.of(1980, 1, 1), "555-0000", "Calle Sol");
        parentDTO = new ParentDTO(10L, "marta_g", "password123", "marta@test.com", person, List.of(100L));
    }

    @Test
    void getAllParentsReturnsOk() throws Exception {
        when(parentService.getAllParentsDTO()).thenReturn(List.of(parentDTO));

        mockMvc.perform(get("/api/parents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].userName").value("marta_g"));
    }

    @Test
    void getParentByIdReturnsOk() throws Exception {
        when(parentService.getParentDTOById(10L)).thenReturn(parentDTO);

        mockMvc.perform(get("/api/parents/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userName").value("marta_g"));
    }

    @Test
    void createParentReturnsCreated() throws Exception {
        when(parentService.createParent(any(ParentDTO.class))).thenReturn(parentDTO);

        String json = """
                {
                    "userName": "marta_g",
                    "password": "password123",
                    "email": "marta@test.com",
                    "person": {
                        "firstName": "Marta",
                        "lastName": "Gomez",
                        "document": "87654321",
                        "birthDate": "1980-01-01"
                    }
                }
                """;

        mockMvc.perform(post("/api/parents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userName").value("marta_g"));
    }

    @Test
    void linkStudentReturnsOk() throws Exception {
        when(parentService.linkStudentDTO(10L, 200L)).thenReturn(parentDTO);

        mockMvc.perform(post("/api/parents/10/students/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void unlinkStudentReturnsOk() throws Exception {
        when(parentService.unlinkStudentDTO(10L, 100L)).thenReturn(parentDTO);

        mockMvc.perform(delete("/api/parents/10/students/100"))
                .andExpect(status().isOk());
    }

    @Test
    void getChildAttendanceSummaryReturnsSummary() throws Exception {
        AttendanceSummary summary = new AttendanceSummary(10L, 2L, 1L, 0L, 83.33);
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 6, 30);

        when(parentAcademicService.getChildAttendanceSummary(10L, 100L, from, to)).thenReturn(summary);

        mockMvc.perform(get("/api/parents/10/children/100/attendances/summary?from=2026-01-01&to=2026-06-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.present").value(10))
                .andExpect(jsonPath("$.absent").value(2))
                .andExpect(jsonPath("$.attendancePercentage").value(83.33));
    }
}
