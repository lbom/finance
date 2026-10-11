package com.finance.personal.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PersonTransactionRepo extends JpaRepository<PersonTransaction, Long> {
    List<PersonTransaction> getPersonalTransactionByPersonIdAndType(
        Long personId,
        PersonTransactionType type
    );

    List<PersonTransaction> findByPersonIdAndTypeAndProfitType(
        Long personId,
        PersonTransactionType type,
        PersonProfitType profitType
    );

    List<PersonTransaction> findByPersonIdAndTypeAndSpendingTypeAndLocalDateBetween(
        Long personId,
        PersonTransactionType type,
        PersonSpendingType spendingType,
        LocalDate startDate,
        LocalDate endDate
    );

    List<PersonTransaction> findByPersonIdAndTypeAndLocalDateBetween(
        Long personId,
        PersonTransactionType type,
        LocalDate startDate,
        LocalDate endDate
    );
}

