package com.finance.personal.transaction.reccurent;

import com.finance.personal.balance.PersonBalanceService;
import com.finance.personal.transaction.PersonTransactionService;
import com.finance.personal.transaction.PersonTransactionType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

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

    @Scheduled(cron = "0 0 0 * * *")
    public void run() {
        var recTrances = personTransactionRecurrentRepo.getPersonTransactionRecurrentByIsActiveTrue();
        for (var recTrans: recTrances) {
            var lastRunDate = recTrans.getLastRunDate();
            var periodDays = recTrans.getPeriodDays();
            if (LocalDateTime.now().isAfter(lastRunDate.plusDays(periodDays))) {
                addTransactionAndUpdateBalance(recTrans);
            }
        }
    }

    private void addTransactionAndUpdateBalance(PersonTransactionRecurrent recurrentTransaction) {
        transactionTemplate.execute(status -> {
            personTransactionService.createAndSavePersonTransaction(
                recurrentTransaction.getPersonId(),
                recurrentTransaction.getBalanceId(),
                recurrentTransaction.getAmount(),
                recurrentTransaction.getType(),
                recurrentTransaction.getProfitType(),
                recurrentTransaction.getSpendingType(),
                recurrentTransaction.getName(),
                java.time.LocalDate.now()
            );

            personBalanceService.update(
                recurrentTransaction.getBalanceId(),
                recurrentTransaction.getAmount(),
                recurrentTransaction.getType() == PersonTransactionType.SPENDING
            );
            return null;
        });
    }

    public void save(PersonTransactionRecurrent transaction) {
        personTransactionRecurrentRepo.save(transaction);
    }

    public void deleteTransaction(Long personId, Long transactionId) {
        personTransactionRecurrentRepo.findById(transactionId)
            .filter(tx -> tx.getPersonId().equals(personId))
            .ifPresent(personTransactionRecurrentRepo::delete);
    }
}
