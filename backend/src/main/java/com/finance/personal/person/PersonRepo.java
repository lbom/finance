package com.finance.personal.person;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepo extends JpaRepository<Person, Long> {
    List<Person> getPersonsByUserId(Long userId);
}
