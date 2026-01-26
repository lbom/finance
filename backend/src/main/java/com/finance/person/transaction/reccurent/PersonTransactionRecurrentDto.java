package com.finance.person.transaction.reccurent;

import com.finance.person.transaction.PersonProfitType;
import com.finance.person.transaction.PersonSpendingType;
import com.finance.person.transaction.PersonTransactionType;

import java.math.BigDecimal;

public record PersonTransactionRecurrentDto(
    Long id,
    Long personId,
    Integer periodDays,
    String name,
    boolean isActive,
    Long balanceId,
    BigDecimal amount,
    PersonTransactionType type,
    PersonSpendingType spendingType,
    PersonProfitType profitType
) {}
