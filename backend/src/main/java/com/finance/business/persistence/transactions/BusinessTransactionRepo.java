package com.finance.business.persistence.transactions;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessTransactionRepo extends JpaRepository<BusinessTransaction, Long> {}