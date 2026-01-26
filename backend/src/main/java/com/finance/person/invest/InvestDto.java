package com.finance.person.invest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestDto(
    Long id,
    Long personId,
    BigDecimal amount,
    LocalDateTime endDate,
    String reason,
    BigDecimal profit,
    LocalDateTime startDate,
    String type,
    Integer symbolId,
    Integer institutionId
) {}