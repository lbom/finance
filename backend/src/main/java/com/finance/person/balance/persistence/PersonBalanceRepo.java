package com.finance.person.balance.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonBalanceRepo extends JpaRepository<PersonBalance, Long> {
    List<PersonBalance> getPersonalBalancesByPersonId(Long userId);
}
