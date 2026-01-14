package com.finance.business.persistence.expenses;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessExpensesRepository extends JpaRepository<BusinessExpenses, Long> {}