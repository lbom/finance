package com.finance.person.transaction.reccurent;

import com.finance.app.UserModule;
import com.finance.person.balance.PersonBalanceService;
import com.finance.person.transaction.PersonTransaction;
import com.finance.person.transaction.PersonTransactionService;
import com.finance.person.transaction.PersonTransactionType;
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
        UserModule userModule, PlatformTransactionManager transactionManager,
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
                recurrentTransaction.getAmount(),
                recurrentTransaction.getType(),
                recurrentTransaction.getProfitType(),
                recurrentTransaction.getSpendingType()
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
}
