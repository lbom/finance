package com.finance.personal.transaction.web.mapper;

import com.finance.personal.transaction.persistence.PersonalProfitType;
import com.finance.personal.transaction.persistence.PersonalSpendingType;
import com.finance.personal.transaction.persistence.PersonalTransactionType;

import java.math.BigDecimal;

public record PersonalTransactionDto(
    Long id,
    PersonalTransactionType type,
    PersonalSpendingType spendingType,
    PersonalProfitType profitType,
    BigDecimal amount
) {}
