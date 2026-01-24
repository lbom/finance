package com.finance.person.transaction.web.mapper;

import com.finance.person.transaction.persistence.PersonProfitType;
import com.finance.person.transaction.persistence.PersonSpendingType;
import com.finance.person.transaction.persistence.PersonTransactionType;

import java.math.BigDecimal;

public record PersonTransactionDto(
    Long id,
    Long personId,
    PersonTransactionType type,
    PersonSpendingType spendingType,
    PersonProfitType profitType,
    BigDecimal amount
) {}
