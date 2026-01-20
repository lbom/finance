package com.finance.personal.transaction.web.mapper;

import com.finance.personal.transaction.persistence.PersonalTransaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonalTransactionMapper {
    PersonalTransactionDto toDto(PersonalTransaction entity);
    PersonalTransaction toEntity(PersonalTransactionDto dto);
    List<PersonalTransactionDto> toDto(List<PersonalTransaction> list);
}
