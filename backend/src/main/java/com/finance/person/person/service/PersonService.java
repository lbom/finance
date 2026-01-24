package com.finance.person.person.service;

import com.finance.app.UserInterface;
import com.finance.person.person.persistence.Person;
import com.finance.person.person.persistence.PersonRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final UserInterface userInterface;
    private final PersonRepo repository;

    public List<Person> getPersons() {
        var userId = userInterface.retrieveUserId();
        return repository.getPersonsByUserId(userId);
    }
    public void save(Person person) {
        var userId = userInterface.retrieveUserId();
        person.setUserId(userId);
        repository.save(person);
    }
}
