package com.finance.personal.transaction.reccurent;

import com.finance.personal.transaction.PersonProfitType;
import com.finance.personal.transaction.PersonSpendingType;
import com.finance.personal.transaction.PersonTransactionType;
import jakarta.annotation.Nonnull;

import java.math.BigDecimal;

public record PersonTransactionRecurrentDto(
    Long id,
    @Nonnull Long personId,
    @Nonnull Integer periodDays,
    @Nonnull String name,
    boolean isActive,
    @Nonnull Long balanceId,
    @Nonnull BigDecimal amount,
    @Nonnull PersonTransactionType type,
    PersonSpendingType spendingType,
    PersonProfitType profitType
) {}
