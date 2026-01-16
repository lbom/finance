package com.finance.business.web.expenses;

import java.math.BigDecimal;

public record BusinessExpensesDto(
    Long id,
    String name,
    BigDecimal amount
) {}

