package com.finance.business.web.transactions;

import com.finance.business.persistence.transactions.BusinessTransactionType;

import java.math.BigDecimal;

public record BusinessTransactionDto(
    Long id,
    String name,
    BigDecimal amount,
    BusinessTransactionType type
) {}

