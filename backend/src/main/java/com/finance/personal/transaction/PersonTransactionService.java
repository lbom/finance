package com.finance.personal.transaction;

import com.finance.personal.balance.PersonBalanceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonTransactionService {

    private final PersonTransactionRepo repository;
    private final PersonBalanceService personBalanceService;

    public List<PersonTransaction> getTransactionsByType(
        Long personId,
        PersonTransactionType type
    ) {
        return repository.getPersonalTransactionByPersonIdAndType(personId, type);
    }

    @Transactional
    public void save(PersonTransaction transaction) {
        repository.save(transaction);
        personBalanceService.update(
            transaction.getBalanceId(),
            transaction.getAmount(),
            transaction.getType() == PersonTransactionType.SPENDING
        );
    }

    @Transactional
    public void createAndSavePersonTransaction(
        Long personId,
        Long balanceId,
        BigDecimal amount,
        PersonTransactionType type,
        PersonProfitType profitType,
        PersonSpendingType spendingType,
        String details,
        LocalDate localDate)
    {
        var personTransaction = new PersonTransaction();
        personTransaction.setPersonId(personId);
        personTransaction.setBalanceId(balanceId);
        personTransaction.setAmount(amount);
        personTransaction.setType(type);
        personTransaction.setProfitType(profitType);
        personTransaction.setSpendingType(spendingType);
        personTransaction.setDetails(details);
        personTransaction.setLocalDate(localDate);
        repository.save(personTransaction);
    }

    @Transactional
    public void deleteTransaction(Long personId, Long transactionId) {
        repository.findById(transactionId)
            .filter(tx -> tx.getPersonId().equals(personId))
            .ifPresent(tx -> {
                repository.delete(tx);
                boolean isSubtract = tx.getType() != PersonTransactionType.SPENDING;
                personBalanceService.update(tx.getBalanceId(), tx.getAmount(), isSubtract);
            });
    }
}
