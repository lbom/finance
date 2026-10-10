package com.finance.personal.transaction.reccurent;

import com.finance.personal.balance.PersonBalanceService;
import com.finance.personal.transaction.PersonProfitType;
import com.finance.personal.transaction.PersonSpendingType;
import com.finance.personal.transaction.PersonTransactionService;
import com.finance.personal.transaction.PersonTransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class PersonTransactionRecurrentService {

    private final PersonBalanceService personBalanceService;
    private final PersonTransactionService personTransactionService;
    private final PersonTransactionRecurrentRepo personTransactionRecurrentRepo;
    private final TransactionTemplate transactionTemplate;

    public PersonTransactionRecurrentService(
        PlatformTransactionManager transactionManager,
        PersonBalanceService personBalanceService,
        PersonTransactionService personTransactionService,
        PersonTransactionRecurrentRepo personTransactionRecurrentRepo
    ){
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.personBalanceService = personBalanceService;
        this.personTransactionService = personTransactionService;
        this.personTransactionRecurrentRepo = personTransactionRecurrentRepo;
    }

    public List<PersonTransactionRecurrent> getTransactionsByPersonId(Long personId) {
        return personTransactionRecurrentRepo.getPersonTransactionRecurrentByPersonId(personId);
    }

    public BigDecimal sumActiveSubscriptionsAverageMonthly(Long personId) {
        var transactions = personTransactionRecurrentRepo
            .getPersonTransactionRecurrentByPersonIdAndIsActiveTrue(personId);
        if (transactions == null || transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (var transaction : transactions) {
            if (transaction.getAmount() == null) continue;
            if (transaction.getPeriodDays() == null || transaction.getPeriodDays() <= 0) continue;
            if (transaction.getSpendingType() != PersonSpendingType.SUBSCRIPTION) continue;
            if (transaction.getType() != PersonTransactionType.SPENDING) continue;

            var periodDays = BigDecimal.valueOf(transaction.getPeriodDays());
            var monthlyAverage = transaction.getAmount()
                .multiply(BigDecimal.valueOf(30))
                .divide(periodDays, 8, RoundingMode.HALF_UP);
            total = total.add(monthlyAverage);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void run() {
        processAllRecurrentTransactions(false);
    }

    public int processAllRecurrentTransactions(boolean force) {
        log.info("Starting recurrent transactions job (force={})", force);
        var recTransactions = personTransactionRecurrentRepo.getPersonTransactionRecurrentByIsActiveTrue();
        int count = 0;
        for (var recTrans: recTransactions) {
            if (isTransactionDue(recTrans, force)) {
                addTransactionAndUpdateBalance(recTrans);
                count++;
            }
        }
        log.info("Completed recurrent transactions job. Processed {} transactions", count);
        return count;
    }

    public int processRecurrentTransactionsForPerson(Long personId, boolean force) {
        log.info("Processing recurrent transactions for personId={} (force={})", personId, force);
        var recTransactions = personTransactionRecurrentRepo.getPersonTransactionRecurrentByPersonIdAndIsActiveTrue(personId);
        int count = 0;
        for (var recTrans: recTransactions) {
            if (isTransactionDue(recTrans, force)) {
                addTransactionAndUpdateBalance(recTrans);
                count++;
            }
        }
        log.info("Completed recurrent transactions for personId={}. Processed {}", personId, count);
        return count;
    }

    public int processSalaryTransactions(boolean force) {
        log.info("Starting salary transactions job (force={})", force);
        var recTransactions = personTransactionRecurrentRepo.getPersonTransactionRecurrentByIsActiveTrue()
                .stream()
                .filter(tx -> tx.getProfitType() == PersonProfitType.SALARY)
                .toList();
        int count = 0;
        for (var recTrans: recTransactions) {
            if (isTransactionDue(recTrans, force)) {
                addTransactionAndUpdateBalance(recTrans);
                count++;
            }
        }
        log.info("Completed salary transactions job. Processed {} salary transactions", count);
        return count;
    }

    private boolean isTransactionDue(PersonTransactionRecurrent recTrans, boolean force) {
        if (force) {
            return true;
        }

        var lastRunDate = recTrans.getLastRunDate();
        if (lastRunDate == null) {
            return true;
        }

        int periodDays = recTrans.getPeriodDays() != null && recTrans.getPeriodDays() > 0 ? recTrans.getPeriodDays() : 30;
        return LocalDateTime.now().isAfter(lastRunDate.plusDays(periodDays));
    }

    private void addTransactionAndUpdateBalance(PersonTransactionRecurrent recurrentTransaction) {
        transactionTemplate.execute(status -> {
            BigDecimal amount = recurrentTransaction.getAmount();

            if ((amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
                    && recurrentTransaction.getInterestRate() != null
                    && recurrentTransaction.getInterestRate().compareTo(BigDecimal.ZERO) > 0) {
                var bal = personBalanceService.findById(recurrentTransaction.getBalanceId());
                if (bal.isPresent() && bal.get().getAmount() != null) {
                    BigDecimal yearlyRate = recurrentTransaction.getInterestRate().divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
                    int days = recurrentTransaction.getPeriodDays() != null && recurrentTransaction.getPeriodDays() > 0
                            ? recurrentTransaction.getPeriodDays()
                            : 30;
                    amount = bal.get().getAmount().multiply(yearlyRate).multiply(BigDecimal.valueOf(days))
                            .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
                }
            }

            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return null;
            }

            personTransactionService.createAndSavePersonTransaction(
                recurrentTransaction.getPersonId(),
                recurrentTransaction.getBalanceId(),
                amount,
                recurrentTransaction.getType(),
                recurrentTransaction.getProfitType(),
                recurrentTransaction.getSpendingType(),
                recurrentTransaction.getDetails() != null && !recurrentTransaction.getDetails().isBlank()
                    ? recurrentTransaction.getDetails()
                    : recurrentTransaction.getName(),
                java.time.LocalDate.now()
            );

            personBalanceService.update(
                recurrentTransaction.getBalanceId(),
                amount,
                recurrentTransaction.getType() == PersonTransactionType.SPENDING
            );

            recurrentTransaction.setLastRunDate(LocalDateTime.now());
            personTransactionRecurrentRepo.save(recurrentTransaction);
            return null;
        });
    }

    public void save(PersonTransactionRecurrent transaction) {
        if (transaction.getLastRunDate() == null) {
            transaction.setLastRunDate(LocalDateTime.now());
        }
        personTransactionRecurrentRepo.save(transaction);
    }

    public void deleteTransaction(Long personId, Long transactionId) {
        personTransactionRecurrentRepo.findById(transactionId)
            .filter(tx -> tx.getPersonId().equals(personId))
            .ifPresent(personTransactionRecurrentRepo::delete);
    }
}
