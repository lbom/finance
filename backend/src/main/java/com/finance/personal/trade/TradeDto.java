package com.finance.personal.trade;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeDto(
    Long id,
    @Nonnull Long personId,
    @Nonnull TradeType type,
    @Nonnull Integer institutionId,
    @Nonnull Integer symbolId,
    @Nonnull String reason,
    @Nonnull BigDecimal amount,
    BigDecimal profit,
    @Nonnull LocalDateTime startDate,
    LocalDateTime endDate
) {}