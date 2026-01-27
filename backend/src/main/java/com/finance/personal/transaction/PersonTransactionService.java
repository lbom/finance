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

    @Transactional
    public void updateTransaction(
        Long personId,
        Long transactionId,
        PersonTransactionDto dto
    ) {
        repository.findById(transactionId)
            .filter(tx -> tx.getPersonId().equals(personId))
            .ifPresent(tx -> {
                boolean reverseSubtract = tx.getType() != PersonTransactionType.SPENDING;
                personBalanceService.update(tx.getBalanceId(), tx.getAmount(), reverseSubtract);

                tx.setBalanceId(dto.balanceId());
                tx.setAmount(dto.amount());
                tx.setType(dto.type());
                tx.setProfitType(dto.profitType());
                tx.setSpendingType(dto.spendingType());
                tx.setDetails(dto.details());
                tx.setLocalDate(dto.localDate());
                repository.save(tx);

                boolean applySubtract = dto.type() == PersonTransactionType.SPENDING;
                personBalanceService.update(dto.balanceId(), dto.amount(), applySubtract);
            });
    }
}
