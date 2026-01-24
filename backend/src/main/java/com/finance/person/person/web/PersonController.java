package com.finance.person.person.web;

import com.finance.app.UserInterface;
import com.finance.person.person.service.PersonService;
import com.finance.person.person.web.mapper.PersonDto;
import com.finance.person.person.web.mapper.PersonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;
    private final PersonMapper mapper;
    private final UserInterface userInterface;

    @GetMapping
    public List<PersonDto> getPersons() {
        var transactions = service.getPersons();
        return mapper.toDto(transactions);
    }

    @PostMapping
    public void add(@RequestBody PersonDto dto) {
        var person = mapper.toEntity(dto);
        var userId = userInterface.retrieveUserId();
        person.setUserId(userId);
        service.save(person);
    }
}