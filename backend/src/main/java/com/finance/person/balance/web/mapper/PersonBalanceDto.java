package com.finance.person.balance.web.mapper;

import java.math.BigDecimal;

public record PersonBalanceDto(
    Long id,
    Long currencyId,
    BigDecimal amount
) {}
