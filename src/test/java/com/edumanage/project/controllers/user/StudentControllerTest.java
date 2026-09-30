package com.edumanage.project.controllers.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.edumanage.project.dtos.academic.MarkDTO;
import com.edumanage.project.dtos.user.StudentDTO;
import com.edumanage.project.exceptions.GlobalExceptionHandler;
import com.edumanage.project.models.academic.performance.Mark;
import com.edumanage.project.services.academic.AttendanceService;
import com.edumanage.project.services.academic.CourseService;
import com.edumanage.project.services.academic.ScheduleService;
import com.edumanage.project.services.academic.StudentAcademicService;
import com.edumanage.project.services.academic.performance.MarkService;
import com.edumanage.project.services.user.StudentService;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @Mock
    private StudentAcademicService studentAcademicService;

    @Mock
    private CourseService courseService;

    @Mock
    private MarkService markService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private StudentController studentController;

    private StudentDTO studentDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        studentDTO = new StudentDTO(1L, "estudiante1", "password123", "estudiante1@test.com",
                "Juan", "A.", "Perez", "12345678", LocalDate.of(2010, 5, 10),
                "555-1111", "Direccion 1", 1L);
    }

    @Test
    void getAllStudentsReturnsOk() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(studentDTO));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("estudiante1"))
                .andExpect(jsonPath("$[0].firstName").value("Juan"));
    }

    @Test
    void getStudentByIdReturnsOk() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(studentDTO);

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Juan"));
    }

    @Test
    void createStudentReturnsCreated() throws Exception {
        when(studentService.createStudent(any(StudentDTO.class))).thenReturn(studentDTO);

        String json = """
                {
                    "username": "estudiante1",
                    "password": "password123",
                    "email": "estudiante1@test.com",
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "dni": "12345678",
                    "birthDate": "2010-05-10",
                    "gradeId": 1
                }
                """;

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("estudiante1"));
    }

    @Test
    void updateStudentReturnsOk() throws Exception {
        when(studentService.updateStudent(eq(1L), any(StudentDTO.class))).thenReturn(studentDTO);

        String json = """
                {
                    "firstName": "Juan Carlos"
                }
                """;

        mockMvc.perform(put("/api/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void deleteStudentReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());

        verify(studentService).deleteStudent(1L);
    }

    @Test
    void getStudentMarksReturnsMarks() throws Exception {
        Mark mark = new Mark();
        mark.setId(10L);
        mark.setMarkValue(4.5);
        MarkDTO markDTO = new MarkDTO(10L, 1L, 2L, 4.5, null);

        when(studentAcademicService.getMarks(1L)).thenReturn(List.of(mark));
        when(markService.convertToDTO(mark)).thenReturn(markDTO);

        mockMvc.perform(get("/api/students/1/marks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].markValue").value(4.5));
    }
}
