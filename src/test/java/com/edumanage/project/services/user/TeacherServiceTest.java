package com.edumanage.project.services.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.edumanage.project.dtos.user.TeacherDTO;
import com.edumanage.project.models.enums.RoleList;
import com.edumanage.project.models.enums.UserStatus;
import com.edumanage.project.models.user.Person;
import com.edumanage.project.models.user.Role;
import com.edumanage.project.models.user.Teacher;
import com.edumanage.project.models.user.User;
import com.edumanage.project.repository.user.RoleRepository;
import com.edumanage.project.repository.user.TeacherRepository;
import com.edumanage.project.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Role teacherRole;
    private Teacher teacher;
    private User user;
    private Person person;

    @BeforeEach
    void setUp() {
        teacherRole = new Role(2L, RoleList.TEACHER);
        user = new User(10L, "carlos", "pass12345", "carlos@example.com", teacherRole, UserStatus.ACTIVE);
        person = new Person();
        person.setId(20L);
        person.setUser(user);
        person.setFirstName("Carlos");
        person.setMiddleName("A.");
        person.setLastName("Perez");
        person.setDocument("12345678");
        person.setPhone("555-1234");
        person.setAddress("Calle 1");
        person.setBirthDate(LocalDate.of(1985, 5, 20));
        teacher = new Teacher(30L, person, "Matemáticas");
    }

    @Test
    void getAllTeachersReturnsList() {
        when(teacherRepository.findAll()).thenReturn(List.of(teacher));

        List<TeacherDTO> result = teacherService.getAllTeachers();

        assertEquals(1, result.size());
        assertEquals("Carlos", result.get(0).getFirstName());
        assertEquals("Matemáticas", result.get(0).getSpeciality());
        assertEquals(30L, result.get(0).getId());
    }

    @Test
    void getTeacherByIdReturnsTeacher() {
        when(teacherRepository.findById(30L)).thenReturn(Optional.of(teacher));

        TeacherDTO result = teacherService.getTeacherById(30L);

        assertNotNull(result);
        assertEquals("Carlos", result.getFirstName());
        assertEquals("carlos@example.com", result.getEmail());
    }

    @Test
    void createTeacherSuccess() {
        TeacherDTO dto = new TeacherDTO(null, "carlos", "pass12345", "carlos@example.com",
                "Carlos", "A.", "Perez", "12345678", LocalDate.of(1985, 5, 20), "555-1234", "Calle 1", "Matemáticas");

        when(userRepository.existsByEmail("carlos@example.com")).thenReturn(false);
        when(roleRepository.findByRol(RoleList.TEACHER)).thenReturn(Optional.of(teacherRole));

        TeacherDTO result = teacherService.createTeacher(dto);

        assertNotNull(result);
        assertEquals("Carlos", result.getFirstName());
        assertEquals("Matemáticas", result.getSpeciality());
        verify(userRepository).save(any(User.class));
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    void createTeacherDuplicateEmailThrows() {
        TeacherDTO dto = new TeacherDTO(null, "carlos", "pass12345", "carlos@example.com",
                "Carlos", "A.", "Perez", "12345678", LocalDate.of(1985, 5, 20), "555-1234", "Calle 1", "Matemáticas");

        when(userRepository.existsByEmail("carlos@example.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> teacherService.createTeacher(dto));
        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void deleteTeacherDeactivatesUser() {
        when(teacherRepository.findById(30L)).thenReturn(Optional.of(teacher));

        teacherService.deleteTeacher(30L);

        assertEquals(UserStatus.INACTIVE, user.getStatus());
        verify(userRepository).save(user);
    }
}
