package com.finance.person.transaction;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonTransactionMapper {
    PersonTransactionDto toDto(PersonTransaction entity);
    PersonTransaction toEntity(PersonTransactionDto dto);
    List<PersonTransactionDto> toDto(List<PersonTransaction> list);
}
