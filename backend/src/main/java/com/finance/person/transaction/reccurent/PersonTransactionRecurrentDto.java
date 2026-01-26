package com.finance.person.transaction.reccurent;

import com.finance.person.transaction.PersonProfitType;
import com.finance.person.transaction.PersonSpendingType;
import com.finance.person.transaction.PersonTransactionType;
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
