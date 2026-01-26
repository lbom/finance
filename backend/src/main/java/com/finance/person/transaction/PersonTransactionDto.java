package com.finance.person.transaction;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;

public record PersonTransactionDto(
    Long id,
    @Nonnull Long personId,
    @Nonnull PersonTransactionType type,
    PersonSpendingType spendingType,
    PersonProfitType profitType,
    BigDecimal amount
) {}
