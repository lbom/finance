package com.finance.personal.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalExpensesRepository extends JpaRepository<PersonalExpenses, Long> {}
