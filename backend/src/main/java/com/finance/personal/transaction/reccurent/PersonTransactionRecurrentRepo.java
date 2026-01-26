package com.finance.personal.transaction.reccurent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonTransactionRecurrentRepo extends JpaRepository<PersonTransactionRecurrent, Long> {
    List<PersonTransactionRecurrent> getPersonTransactionRecurrentByPersonId(
        Long personId
    );

    List<PersonTransactionRecurrent> getPersonTransactionRecurrentByIsActiveTrue();
}
