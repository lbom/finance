package com.finance.business.web.core;

import java.math.BigDecimal;

public record BusinessDto(
    Long id,
    String name,
    String idea,
    BigDecimal overallAmount,
    BigDecimal overallProfit
) {}

