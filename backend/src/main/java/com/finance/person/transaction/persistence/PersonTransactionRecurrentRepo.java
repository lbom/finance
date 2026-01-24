package com.finance.person.transaction.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonTransactionRecurrentRepo extends JpaRepository<PersonTransactionReccurent, Long> {
    List<PersonTransactionReccurent> getPersonTransactionRecurrentByPersonId(
        Long personId
    );

    List<PersonTransactionReccurent> getPersonTransactionRecurrentByIsActiveTrue();
}
