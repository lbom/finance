package com.finance.dictionary.symbol;

import com.finance.person.PersonDto;
import com.finance.person.person.Person;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SymbolMapper {
    SymbolDto toDto(Symbol entity);
    Symbol toEntity(PersonDto dto);
    List<PersonDto> toDto(List<Symbol> list);
}
