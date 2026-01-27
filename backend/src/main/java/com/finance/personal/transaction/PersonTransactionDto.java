package com.finance.personal.transaction;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PersonTransactionDto(
    Long id,
    @Nonnull Long personId,
    @Nonnull Long balanceId,
    @Nonnull LocalDate localDate,
    @Nonnull String details,
    @Nonnull PersonTransactionType type,
    PersonSpendingType spendingType,
    PersonProfitType profitType,
    BigDecimal amount
) {}
