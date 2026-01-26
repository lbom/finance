package com.finance.person.trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeDto(
    Long id,
    Long personId,
    TradeType type,
    Integer institutionId,
    Integer symbolId,
    String reason,
    BigDecimal amount,
    BigDecimal profit,
    LocalDateTime startDate,
    LocalDateTime endDate
) {}