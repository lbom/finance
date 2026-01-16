package com.finance.business.web.expenses;

import com.finance.business.persistence.expenses.BusinessExpenses;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BusinessExpensesMapper {
    BusinessExpensesDto toDto(BusinessExpenses expenses);
    BusinessExpenses toEntity(BusinessExpensesDto dto);
    List<BusinessExpensesDto> toDto(List<BusinessExpenses> list);
}