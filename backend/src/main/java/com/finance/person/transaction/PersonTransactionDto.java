package com.finance.person.transaction;

import java.math.BigDecimal;

public record PersonTransactionDto(
    Long id,
    Long personId,
    PersonTransactionType type,
    PersonSpendingType spendingType,
    PersonProfitType profitType,
    BigDecimal amount
) {}
