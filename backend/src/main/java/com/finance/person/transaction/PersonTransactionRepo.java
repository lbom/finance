package com.finance.person.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonTransactionRepo extends JpaRepository<PersonTransaction, Long> {
    List<PersonTransaction> getPersonalTransactionByPersonIdAndType(
        Long personId,
        PersonTransactionType type
    );
}
