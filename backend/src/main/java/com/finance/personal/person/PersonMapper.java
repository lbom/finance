package com.finance.personal.person;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    PersonDto toDto(Person entity);
    Person toEntity(PersonDto dto);
    List<PersonDto> toDto(List<Person> list);
}
