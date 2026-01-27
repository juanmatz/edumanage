package com.edumanage.project.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumanage.project.models.user.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
