package com.finance.personal.web.mapper;

import com.finance.personal.persistence.PersonalExpensesType;

import java.math.BigDecimal;

public record PersonalExpenseDto(
    Long id,
    PersonalExpensesType expenseType,
    BigDecimal amount
) {}
