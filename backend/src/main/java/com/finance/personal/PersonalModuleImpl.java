package com.finance.personal;

import com.finance.personal.person.PersonRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalModuleImpl implements PersonalModule {

    private final PersonRepo personRepo;

    @Override
    public List<PersonModuleDto> getPersonsByUserId(Long userId) {
        var persons = personRepo.getPersonsByUserId(userId);
        return persons.stream().map(
                v -> new PersonModuleDto(
                            v.getId(),
                            v.getUserId(),
                            v.getName()
                        )).toList();
    }
}