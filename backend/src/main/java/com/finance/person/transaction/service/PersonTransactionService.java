package com.finance.person.transaction.service;

import com.finance.app.UserInterface;
import com.finance.person.transaction.persistence.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonTransactionService {

    private final UserInterface userInterface;
    private final PersonTransactionRepo repository;

    public List<PersonTransaction> getTransactionsByType(PersonTransactionType type) {
        var userId = userInterface.retrieveUserId();
        return repository.getPersonalTransactionByPersonIdAndType(userId, type);
    }

    public void save(PersonTransaction transaction) {
        repository.save(transaction);
    }

    @Transactional
    public void createAndSavePersonTransaction(
        Long personId,
        BigDecimal amount,
        PersonTransactionType type,
        PersonProfitType profitType,
        PersonSpendingType spendingType)
    {
        var personTransaction = new PersonTransaction();
        personTransaction.setPersonId(personId);
        personTransaction.setAmount(amount);
        personTransaction.setType(type);
        personTransaction.setProfitType(profitType);
        personTransaction.setSpendingType(spendingType);
        repository.save(personTransaction);
    }
}
