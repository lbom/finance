package com.finance.person.transaction;

import com.finance.app.UserModule;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonTransactionService {

    private final PersonTransactionRepo repository;

    public List<PersonTransaction> getTransactionsByType(
        Long personId,
        PersonTransactionType type
    ) {
        return repository.getPersonalTransactionByPersonIdAndType(personId, type);
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
