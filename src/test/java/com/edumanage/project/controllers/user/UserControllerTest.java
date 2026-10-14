package com.edumanage.project.controllers.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

import com.edumanage.project.dtos.user.UserDTO;
import com.edumanage.project.exceptions.GlobalExceptionHandler;
import com.edumanage.project.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllUsersReturnsOk() throws Exception {
        UserDTO dto = new UserDTO("juan", "password123", "juan@test.com", "ADMIN", "ACTIVE");
        dto.setId(1L);
        when(userService.getAllUsersDTO()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userName").value("juan"))
                .andExpect(jsonPath("$[0].email").value("juan@test.com"));
    }

    @Test
    void getUserByIdReturnsUser() throws Exception {
        UserDTO dto = new UserDTO("juan", "password123", "juan@test.com", "ADMIN", "ACTIVE");
        dto.setId(1L);
        when(userService.getUserDTOById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("juan"));
    }

    @Test
    void createUserReturnsCreated() throws Exception {
        UserDTO dto = new UserDTO("juan", "password123", "juan@test.com", "ADMIN", "ACTIVE");
        dto.setId(1L);
        when(userService.saveUser(any(UserDTO.class))).thenReturn(dto);

        String json = """
                {
                    "userName": "juan",
                    "password": "password123",
                    "email": "juan@test.com",
                    "role": "ADMIN",
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("juan"));
    }

    @Test
    void updateUserReturnsOk() throws Exception {
        UserDTO updated = new UserDTO("juan_updated", "password123", "juan@test.com", "ADMIN", "ACTIVE");
        updated.setId(1L);
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(updated);

        String json = """
                {
                    "userName": "juan_updated",
                    "email": "juan@test.com"
                }
                """;

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("juan_updated"));
    }

    @Test
    void deleteUserReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void changePasswordReturnsOk() throws Exception {
        String json = """
                {
                    "currentPassword": "oldPassword123",
                    "newPassword": "newPassword123"
                }
                """;

        mockMvc.perform(patch("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        verify(userService).changePassword(1L, "oldPassword123", "newPassword123");
    }
}
