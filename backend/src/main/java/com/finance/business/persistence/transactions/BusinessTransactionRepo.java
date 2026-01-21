package com.finance.business.persistence.transactions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessTransactionRepo extends JpaRepository<BusinessTransaction, Long> {
    List<BusinessTransaction> getBusinessTransactionsByUserIdAndType(
        Long userId,
        BusinessTransactionType type
    );
}