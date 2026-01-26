package com.finance.personal.transaction;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;

public record PersonTransactionDto(
    Long id,
    @Nonnull Long personId,
    @Nonnull Long balanceId,
    @Nonnull PersonTransactionType type,
    PersonSpendingType spendingType,
    PersonProfitType profitType,
    BigDecimal amount
) {}
