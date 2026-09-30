package com.edumanage.project.services.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParentService {
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    public List<ParentDTO> getAllParentsDTO() {
        return parentRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Parent getParentById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
    }

    public ParentDTO getParentDTOById(Long id) {
        return convertToDTO(getParentById(id));
    }

    public ParentDTO createParent(ParentDTO dto) {
        validateCreateData(dto);
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUserName(dto.getUserName().trim());
        user.setEmail(dto.getEmail().trim());
        user.setPassword(dto.getPassword());
        user.setStatus(UserStatus.ACTIVE);

        Role parentRole = roleRepository.findByRol(RoleList.PARENT)
                .orElseThrow(() -> new RuntimeException("PARENT role is not configured in the database"));
        user.setRole(parentRole);

        userRepository.save(user);

        Person person = new Person();
        PersonDTO pDto = dto.getPerson();
        person.setFirstName(pDto.getFirstName().trim());
        person.setMiddleName(pDto.getMiddleName() != null ? pDto.getMiddleName().trim() : null);
        person.setLastName(pDto.getLastName().trim());
        person.setDocument(pDto.getDocument().trim());
        person.setBirthDate(pDto.getBirthDate());
        person.setPhone(pDto.getPhone());
        person.setAddress(pDto.getAddress());
        person.setUser(user);

        Parent parent = new Parent();
        parent.setPerson(person);

        List<Student> students = new ArrayList<>();
        if (dto.getStudentIds() != null) {
            for (Long studentId : dto.getStudentIds()) {
                Student s = studentRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
                students.add(s);
            }
        }
        parent.setStudents(students);

        parentRepository.save(parent);
        return convertToDTO(parent);
    }

    public ParentDTO updateParent(Long id, ParentDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parent data is required");
        }
        Parent parent = getParentById(id);
        Person person = parent.getPerson();
        User user = person.getUser();

        if (dto.getUserName() != null && !dto.getUserName().isBlank()) {
            user.setUserName(dto.getUserName().trim());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            validateEmail(dto.getEmail());
            if (!dto.getEmail().trim().equalsIgnoreCase(user.getEmail())
                    && userRepository.existsByEmail(dto.getEmail().trim())) {
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

        if (dto.getPerson() != null) {
            PersonDTO pDto = dto.getPerson();
            if (pDto.getFirstName() != null && !pDto.getFirstName().isBlank()) {
                person.setFirstName(pDto.getFirstName().trim());
            }
            if (pDto.getMiddleName() != null) {
                person.setMiddleName(pDto.getMiddleName().trim());
            }
            if (pDto.getLastName() != null && !pDto.getLastName().isBlank()) {
                person.setLastName(pDto.getLastName().trim());
            }
            if (pDto.getDocument() != null && !pDto.getDocument().isBlank()) {
                person.setDocument(pDto.getDocument().trim());
            }
            if (pDto.getBirthDate() != null) {
                if (!pDto.getBirthDate().isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("Birth date must be in the past");
                }
                person.setBirthDate(pDto.getBirthDate());
            }
            if (pDto.getPhone() != null) {
                person.setPhone(pDto.getPhone().trim());
            }
            if (pDto.getAddress() != null) {
                person.setAddress(pDto.getAddress().trim());
            }
        }

        if (dto.getStudentIds() != null) {
            List<Student> students = new ArrayList<>();
            for (Long studentId : dto.getStudentIds()) {
                Student s = studentRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
                students.add(s);
            }
            parent.setStudents(students);
        }

        userRepository.save(user);
        parentRepository.save(parent);
        return convertToDTO(parent);
    }

    public Parent linkStudent(Long parentId, Long studentId) {
        Parent parent = getParentById(parentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        if (parent.getStudents() == null) {
            parent.setStudents(new ArrayList<>());
        }
        if (!parent.getStudents().contains(student)) {
            parent.getStudents().add(student);
        }
        return parentRepository.save(parent);
    }

    public ParentDTO linkStudentDTO(Long parentId, Long studentId) {
        return convertToDTO(linkStudent(parentId, studentId));
    }

    public Parent unlinkStudent(Long parentId, Long studentId) {
        Parent parent = getParentById(parentId);
        if (parent.getStudents() != null) {
            parent.getStudents().removeIf(student -> studentId.equals(student.getId()));
        }
        return parentRepository.save(parent);
    }

    public ParentDTO unlinkStudentDTO(Long parentId, Long studentId) {
        return convertToDTO(unlinkStudent(parentId, studentId));
    }

    public void deleteParent(Long id) {
        Parent parent = getParentById(id);
        if (parent.getPerson() != null && parent.getPerson().getUser() != null) {
            User user = parent.getPerson().getUser();
            user.setStatus(UserStatus.INACTIVE);
            userRepository.save(user);
        }
    }

    public ParentDTO convertToDTO(Parent parent) {
        if (parent == null) {
            return null;
        }
        ParentDTO dto = new ParentDTO();
        dto.setId(parent.getId());
        if (parent.getPerson() != null) {
            Person person = parent.getPerson();
            if (person.getUser() != null) {
                dto.setUserName(person.getUser().getUserName());
                dto.setEmail(person.getUser().getEmail());
            }
            PersonDTO personDTO = new PersonDTO();
            personDTO.setId(person.getId());
            personDTO.setFirstName(person.getFirstName());
            personDTO.setMiddleName(person.getMiddleName());
            personDTO.setLastName(person.getLastName());
            personDTO.setDocument(person.getDocument());
            personDTO.setBirthDate(person.getBirthDate());
            personDTO.setPhone(person.getPhone());
            personDTO.setAddress(person.getAddress());
            dto.setPerson(personDTO);
        }
        if (parent.getStudents() != null) {
            dto.setStudentIds(parent.getStudents().stream().map(Student::getId).toList());
        } else {
            dto.setStudentIds(new ArrayList<>());
        }
        return dto;
    }

    private void validateCreateData(ParentDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parent data is required");
        }
        validateText(dto.getUserName(), "Username");
        validateText(dto.getEmail(), "Email");
        validateEmail(dto.getEmail());
        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (dto.getPerson() == null) {
            throw new IllegalArgumentException("Personal data is required");
        }
        PersonDTO p = dto.getPerson();
        validateText(p.getFirstName(), "First name");
        validateText(p.getLastName(), "Last name");
        validateText(p.getDocument(), "Document/DNI");
        if (p.getBirthDate() == null) {
            throw new IllegalArgumentException("Birth date is required");
        }
        if (!p.getBirthDate().isBefore(LocalDate.now())) {
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
