package com.finance.personal.balance;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;

public record PersonBalanceDto(
    Long id,
    @Nonnull Long personId,
    @Nonnull Long currencyId,
    @Nonnull BigDecimal amount
) {}
