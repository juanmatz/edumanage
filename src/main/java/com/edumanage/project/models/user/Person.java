package com.edumanage.project.models.user;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Persons")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long id;

    //clave foranea para user id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "document", nullable = false)
    private int document;
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    public Person(Long id, String firstName, String lastName, int document, LocalDate birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.birthDate = birthDate;
    }
}
