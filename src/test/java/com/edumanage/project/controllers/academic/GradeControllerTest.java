package com.edumanage.project.controllers.academic;

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

import com.edumanage.project.dtos.academic.GradeDTO;
import com.edumanage.project.exceptions.GlobalExceptionHandler;
import com.edumanage.project.services.academic.GradeService;

@ExtendWith(MockitoExtension.class)
class GradeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GradeService gradeService;

    @InjectMocks
    private GradeController gradeController;

    private GradeDTO gradeDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gradeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        gradeDTO = new GradeDTO(1L, "Primero de Secundaria", 30);
    }

    @Test
    void getAllGradesReturnsOk() throws Exception {
        when(gradeService.getAllGradesDTO()).thenReturn(List.of(gradeDTO));

        mockMvc.perform(get("/api/grades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].gradeName").value("Primero de Secundaria"))
                .andExpect(jsonPath("$[0].maxCapacity").value(30));
    }

    @Test
    void getGradeByIdReturnsOk() throws Exception {
        when(gradeService.getGradeDTOById(1L)).thenReturn(gradeDTO);

        mockMvc.perform(get("/api/grades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.gradeName").value("Primero de Secundaria"));
    }

    @Test
    void createGradeReturnsCreated() throws Exception {
        when(gradeService.createGrade(any(GradeDTO.class))).thenReturn(gradeDTO);

        String json = """
                {
                    "gradeName": "Primero de Secundaria",
                    "maxCapacity": 30
                }
                """;

        mockMvc.perform(post("/api/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.gradeName").value("Primero de Secundaria"));
    }

    @Test
    void updateGradeReturnsOk() throws Exception {
        when(gradeService.updateGrade(eq(1L), any(GradeDTO.class))).thenReturn(gradeDTO);

        String json = """
                {
                    "gradeName": "Primero de Secundaria B"
                }
                """;

        mockMvc.perform(put("/api/grades/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void deleteGradeReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/grades/1"))
                .andExpect(status().isNoContent());

        verify(gradeService).deleteGrade(1L);
    }
}
