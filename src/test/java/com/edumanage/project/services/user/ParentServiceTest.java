package com.edumanage.project.services.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.edumanage.project.dtos.user.ParentDTO;
import com.edumanage.project.dtos.user.PersonDTO;
import com.edumanage.project.models.enums.RoleList;
import com.edumanage.project.models.enums.UserStatus;
import com.edumanage.project.models.user.Parent;
import com.edumanage.project.models.user.Person;
import com.edumanage.project.models.user.Role;
import com.edumanage.project.models.user.Student;
import com.edumanage.project.models.user.User;
import com.edumanage.project.repository.user.ParentRepository;
import com.edumanage.project.repository.user.RoleRepository;
import com.edumanage.project.repository.user.StudentRepository;
import com.edumanage.project.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class ParentServiceTest {

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private ParentService parentService;

    private Role parentRole;
    private User user;
    private Person person;
    private Parent parent;
    private Student student;

    @BeforeEach
    void setUp() {
        parentRole = new Role(4L, RoleList.PARENT);
        user = new User(1L, "marta", "password123", "marta@example.com", parentRole, UserStatus.ACTIVE);
        person = new Person();
        person.setId(10L);
        person.setUser(user);
        person.setFirstName("Marta");
        person.setLastName("Gomez");
        person.setDocument("87654321");
        person.setPhone("555-9876");
        person.setAddress("Calle 2");
        person.setBirthDate(LocalDate.of(1980, 3, 15));
        student = new Student(100L, null, null);
        parent = new Parent(50L, person, new ArrayList<>(List.of(student)));
    }

    @Test
    void getAllParentsReturnsList() {
        when(parentRepository.findAll()).thenReturn(List.of(parent));

        List<ParentDTO> result = parentService.getAllParentsDTO();

        assertEquals(1, result.size());
        assertEquals("Andres", result.get(0).getUserName());
        assertEquals(50L, result.get(0).getId());
        assertEquals(List.of(100L), result.get(0).getStudentIds());
    }

    @Test
    void getParentByIdReturnsParentDTO() {
        when(parentRepository.findById(50L)).thenReturn(Optional.of(parent));

        ParentDTO result = parentService.getParentDTOById(50L);

        assertNotNull(result);
        assertEquals("marta", result.getUserName());
        assertEquals("87654321", result.getPerson().getDocument());
    }

    @Test
    void createParentSuccess() {
        PersonDTO personDTO = new PersonDTO(null, "Marta", null, "Gomez", "87654321",
                LocalDate.of(1980, 3, 15), "555-9876", "Calle 2");
        ParentDTO dto = new ParentDTO(null, "marta", "password123", "marta@example.com",
                personDTO, List.of(100L));

        when(userRepository.existsByEmail("marta@example.com")).thenReturn(false);
        when(roleRepository.findByRol(RoleList.PARENT)).thenReturn(Optional.of(parentRole));
        when(studentRepository.findById(100L)).thenReturn(Optional.of(student));

        ParentDTO result = parentService.createParent(dto);

        assertNotNull(result);
        assertEquals("marta", result.getUserName());
        assertEquals("marta@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
        verify(parentRepository).save(any(Parent.class));
    }

    @Test
    void linkStudentAddsStudent() {
        Student newStudent = new Student(200L, null, null);
        when(parentRepository.findById(50L)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(200L)).thenReturn(Optional.of(newStudent));
        when(parentRepository.save(any(Parent.class))).thenReturn(parent);

        ParentDTO result = parentService.linkStudentDTO(50L, 200L);

        assertNotNull(result);
        assertTrue(parent.getStudents().contains(newStudent));
    }

    @Test
    void unlinkStudentRemovesStudent() {
        when(parentRepository.findById(50L)).thenReturn(Optional.of(parent));
        when(parentRepository.save(any(Parent.class))).thenReturn(parent);

        ParentDTO result = parentService.unlinkStudentDTO(50L, 100L);

        assertNotNull(result);
        assertTrue(parent.getStudents().isEmpty());
    }

    @Test
    void deleteParentDeactivatesUser() {
        when(parentRepository.findById(50L)).thenReturn(Optional.of(parent));

        parentService.deleteParent(50L);

        assertEquals(UserStatus.INACTIVE, user.getStatus());
        verify(userRepository).save(user);
    }
}
