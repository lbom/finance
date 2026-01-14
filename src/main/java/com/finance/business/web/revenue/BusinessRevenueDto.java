package com.finance.business.web.revenue;

import java.math.BigDecimal;

public record BusinessRevenueDto(
    Long id,
    String name,
    String product,
    BigDecimal amount
) {}