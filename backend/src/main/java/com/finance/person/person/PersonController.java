package com.finance.person.person;

import com.finance.app.UserModule;
import com.finance.person.PersonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;
    private final PersonMapper mapper;
    private final UserModule userModule;

    @GetMapping
    public List<PersonDto> getPersons() {
        var persons = service.getPersons();
        return mapper.toDto(persons);
    }

    @PostMapping
    public void add(@RequestBody PersonDto dto) {
        var person = mapper.toEntity(dto);
        var userId = userModule.retrieveUserId();
        person.setUserId(userId);
        service.save(person);
    }
}