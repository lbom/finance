package com.finance.personal.transaction.reccurent;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonTransactionRecurrentMapper {
    PersonTransactionRecurrentDto toDto(PersonTransactionRecurrent entity);
    PersonTransactionRecurrent toEntity(PersonTransactionRecurrentDto dto);
    List<PersonTransactionRecurrentDto> toDto(List<PersonTransactionRecurrent> list);
}

