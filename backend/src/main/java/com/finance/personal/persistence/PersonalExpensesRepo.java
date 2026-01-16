package com.finance.personal.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalExpensesRepo extends JpaRepository<PersonalExpenses, Long> {}
