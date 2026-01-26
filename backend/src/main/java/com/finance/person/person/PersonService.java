package com.finance.person.person;

import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final UserModule userModule;
    private final PersonRepo repository;

    public List<Person> getPersons() {
        var userId = userModule.retrieveUserId();
        return repository.getPersonsByUserId(userId);
    }
    public void save(Person person) {
        var userId = userModule.retrieveUserId();
        person.setUserId(userId);
        repository.save(person);
    }
}
