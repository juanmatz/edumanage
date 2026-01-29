package com.edumanage.project.services.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.edumanage.project.dtos.user.StudentDTO;
import com.edumanage.project.models.enums.RoleList;
import com.edumanage.project.models.enums.UserStatus;
import com.edumanage.project.models.user.Person;
import com.edumanage.project.models.user.Role;
import com.edumanage.project.models.user.Student;
import com.edumanage.project.models.user.User;
import com.edumanage.project.models.academic.Grade;
import com.edumanage.project.repository.user.RoleRepository;
import com.edumanage.project.repository.user.StudentRepository;
import com.edumanage.project.repository.user.UserRepository;
import com.edumanage.project.repository.academic.GradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GradeRepository gradeRepository;

    public StudentDTO createStudent(StudentDTO dto) {
        if (userRepository.existsByEmail((dto.getEmail()))) {
            throw new RuntimeException("Email already exist");
        }
        User user = new User();
        user.setUserName(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setStatus(UserStatus.ACTIVE);
        Role studentRole = roleRepository.findByRol(RoleList.STUDENT)
                .orElseThrow(() -> new RuntimeException("STUDENT role is not configured in the database"));
        user.setRole(studentRole);

        Person person = new Person();
        person.setFirstName(dto.getFirstName());
        person.setMiddleName(dto.getMiddleName());
        person.setLastName(dto.getLastName());
        person.setDocument(dto.getDni());
        person.setBirthDate(dto.getBirthDate());
        person.setPhone(dto.getPhone());
        person.setAddress(dto.getAddress());
        person.setUser(user);

        Student student = new Student();
        student.setPerson(person);

        Grade grade = gradeRepository.findById(dto.getGradeId())
                .orElseThrow(() -> new RuntimeException("Grade not found with ID: " + dto.getGradeId()));
        student.setGrade(grade);

        studentRepository.save(student);
        return dto;
    }

    public List<StudentDTO> getAllStudents() {
        List<Student> listStudents = studentRepository.findAll();
        List<StudentDTO> result = new ArrayList<>();
        for (Student s : listStudents) {
            StudentDTO dto = convertToDTO(s);
            result.add(dto);
        }
        return result;
    }

    public void deleteStudent(Long id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        User user = s.getPerson().getUser();
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    // metodo que convierte un objeto student en su dto con los datos no sencibles
    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();

        dto.setFirstName(student.getPerson().getFirstName());
        dto.setMiddleName(student.getPerson().getMiddleName());
        dto.setLastName(student.getPerson().getLastName());
        dto.setDni(student.getPerson().getDocument());
        dto.setBirthDate(student.getPerson().getBirthDate());
        dto.setPhone(student.getPerson().getPhone());
        dto.setAddress(student.getPerson().getAddress());

        dto.setEmail(student.getPerson().getUser().getEmail());
        dto.setUsername(student.getPerson().getUser().getUserName());

        if (student.getGrade() != null) {
            dto.setGradeId(student.getGrade().getId());
        }

        return dto;
    }

    public StudentDTO getStudentById(Long id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return convertToDTO(s);
    }

    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not Found"));
        Person p = s.getPerson();
        if (dto.getFirstName() != null)
            p.setFirstName(dto.getFirstName());
        if (dto.getMiddleName() != null)
            p.setMiddleName(dto.getMiddleName());
        if (dto.getLastName() != null)
            p.setLastName(dto.getLastName());
        if (dto.getDni() != null)
            p.setDocument(dto.getDni());
        if (dto.getBirthDate() != null)
            p.setBirthDate(dto.getBirthDate());
        if (dto.getPhone() != null)
            p.setPhone(dto.getPhone());
        if (dto.getAddress() != null)
            p.setAddress(dto.getAddress());

        if (dto.getGradeId() != null) {
            Grade grade = gradeRepository.findById(dto.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Grade not found"));
            s.setGrade(grade);
        }

        studentRepository.save(s);
        return convertToDTO(s);

    }

}
