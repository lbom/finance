package com.finance.business.web.transactions;

import java.math.BigDecimal;

public record BusinessTransactionDto(
    Long id,
    String name,
    BigDecimal amount,
    BusinessTransactionType type
) {}

