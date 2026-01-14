package com.finance.trades.web;

import com.finance.trades.persistence.TradeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeDto(
    Long id,
    TradeType type,
    Integer institutionId,
    Integer symbolId,
    String reason,
    BigDecimal amount,
    BigDecimal profit,
    LocalDateTime startDate,
    LocalDateTime endDate
) {}