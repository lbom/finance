package com.finance.personal.balance;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PersonBalanceDto(
    Long id,
    @Nonnull Long personId,
    @Nonnull Long institutionId,
    @Nonnull Long currencyId,
    @Nonnull BigDecimal amount,
    PersonBalanceType type,
    BigDecimal interestRate,
    LocalDate lastProfitDate
) {}
