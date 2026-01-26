package com.finance.business.transactions;

import java.math.BigDecimal;

public record BusinessTransactionDto(
    Long id,
    String name,
    BigDecimal amount,
    BusinessTransactionType type
) {}

