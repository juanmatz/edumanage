package com.edumanage.project.services.user;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public TeacherDTO createTeacher(TeacherDTO dto) {
        validateCreateData(dto);
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setStatus(UserStatus.ACTIVE);

        Role teacherRole = roleRepository.findByRol(RoleList.TEACHER)
                .orElseThrow(() -> new RuntimeException("TEACHER role is not configured in the database"));
        user.setRole(teacherRole);

        Person person = new Person();
        person.setFirstName(dto.getFirstName());
        person.setMiddleName(dto.getMiddleName());
        person.setLastName(dto.getLastName());
        person.setDocument(dto.getDni());
        person.setBirthDate(dto.getBirthDate());
        person.setPhone(dto.getPhone());
        person.setAddress(dto.getAddress());
        person.setUser(user);

        // Person does not cascade persistence to User, so persist the account first.
        userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setPerson(person);
        teacher.setSpeciality(dto.getSpeciality());

        teacherRepository.save(teacher);
        return convertToDTO(teacher);
    }

    public List<TeacherDTO> getAllTeachers() {
        List<TeacherDTO> result = new ArrayList<>();
        for (Teacher teacher : teacherRepository.findAll()) {
            result.add(convertToDTO(teacher));
        }
        return result;
    }

    public TeacherDTO getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return convertToDTO(teacher);
    }

    public TeacherDTO updateTeacher(Long id, TeacherDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Teacher data is required");
        }
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Person person = teacher.getPerson();
        User user = person.getUser();

        if (dto.getUserName() != null) {
            validateText(dto.getUserName(), "Username");
            user.setUserName(dto.getUserName().trim());
        }
        if (dto.getEmail() != null) {
            validateEmail(dto.getEmail());
            if (!dto.getEmail().equals(user.getEmail())
                    && userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(dto.getEmail().trim());
        }
        if (dto.getPassword() != null) {
            if (dto.getPassword().length() < 8) {
                throw new IllegalArgumentException("Password must be at least 8 characters long");
            }
            user.setPassword(dto.getPassword());
        }

        if (dto.getFirstName() != null) {
            validateText(dto.getFirstName(), "First name");
            person.setFirstName(dto.getFirstName());
        }
        if (dto.getMiddleName() != null) {
            person.setMiddleName(dto.getMiddleName());
        }
        if (dto.getLastName() != null) {
            validateText(dto.getLastName(), "Last name");
            person.setLastName(dto.getLastName());
        }
        if (dto.getDni() != null) {
            validateText(dto.getDni(), "DNI");
            person.setDocument(dto.getDni());
        }
        if (dto.getBirthDate() != null) {
            if (!dto.getBirthDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Birth date must be in the past");
            }
            person.setBirthDate(dto.getBirthDate());
        }
        if (dto.getPhone() != null) {
            person.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            person.setAddress(dto.getAddress());
        }
        if (dto.getSpeciality() != null) {
            validateText(dto.getSpeciality(), "Speciality");
            teacher.setSpeciality(dto.getSpeciality().trim());
        }

        userRepository.save(user);
        teacherRepository.save(teacher);
        return convertToDTO(teacher);
    }

    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        User user = teacher.getPerson().getUser();
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    private TeacherDTO convertToDTO(Teacher teacher) {
        Person person = teacher.getPerson();
        User user = person.getUser();

        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setFirstName(person.getFirstName());
        dto.setMiddleName(person.getMiddleName());
        dto.setLastName(person.getLastName());
        dto.setDni(person.getDocument());
        dto.setBirthDate(person.getBirthDate());
        dto.setPhone(person.getPhone());
        dto.setAddress(person.getAddress());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setSpeciality(teacher.getSpeciality());
        return dto;
    }

    private void validateCreateData(TeacherDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Teacher data is required");
        }
        validateText(dto.getUserName(), "Username");
        validateText(dto.getEmail(), "Email");
        validateEmail(dto.getEmail());
        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        validateText(dto.getFirstName(), "First name");
        validateText(dto.getLastName(), "Last name");
        validateText(dto.getDni(), "DNI");
        validateText(dto.getSpeciality(), "Speciality");
        if (dto.getBirthDate() == null) {
            throw new IllegalArgumentException("Birth date is required");
        }
        if (!dto.getBirthDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date must be in the past");
        }
    }

    private void validateText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be blank");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Email must be valid");
        }
    }
}
