package com.finance.person.invest;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestDto(
    Long id,
    @Nonnull Long personId,
    @Nonnull BigDecimal amount,
    LocalDateTime endDate,
    @Nonnull String reason,
    BigDecimal profit,
    @Nonnull LocalDateTime startDate,
    InvestType type,
    @Nonnull Integer symbolId,
    @Nonnull Integer institutionId
) {}