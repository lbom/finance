package com.finance.person.balance;

import java.math.BigDecimal;

public record PersonBalanceDto(
    Long id,
    Long personId,
    Long currencyId,
    BigDecimal amount
) {}
