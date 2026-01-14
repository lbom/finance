package com.finance.personal.web.mapper;

import com.finance.personal.persistence.PersonalExpenses;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonalExpensesMapper {
    PersonalExpenseDto toDto(PersonalExpenses entity);
    PersonalExpenses toEntity(PersonalExpenseDto dto);
    List<PersonalExpenseDto> toDto(List<PersonalExpenses> list);
}
