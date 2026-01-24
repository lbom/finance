package com.finance.person.transaction.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonTransactionRepo extends JpaRepository<PersonTransaction, Long> {
    List<PersonTransaction> getPersonalTransactionByPersonIdAndType(
        Long userId,
        PersonTransactionType type
    );
}
