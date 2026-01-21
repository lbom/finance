package com.finance.personal.transaction.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalTransactionRepo extends JpaRepository<PersonalTransaction, Long> {
    List<PersonalTransaction> getPersonalTransactionByUserIdAndType(
        Long userId,
        PersonalTransactionType type
    );
}
