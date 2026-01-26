package com.finance.person;

import com.finance.person.person.PersonMapper;
import com.finance.person.person.PersonRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonModuleImpl implements PersonModule {

    private final PersonRepo personRepo;
    private final PersonMapper personMapper;

    @Override
    public List<PersonDto> getPersonsByUserId(Long userId) {
        var persons = personRepo.getPersonsByUserId(userId);
        return personMapper.toDto(persons);
    }
}