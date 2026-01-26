package com.finance.business.core;

import java.math.BigDecimal;

public record BusinessDto(
    Long id,
    String name,
    String idea,
    BigDecimal expense,
    BigDecimal revenue
) {}

