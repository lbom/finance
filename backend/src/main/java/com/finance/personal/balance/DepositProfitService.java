package com.finance.personal.balance;

import com.finance.fx.FxModule;
import com.finance.personal.transaction.PersonProfitType;
import com.finance.personal.transaction.PersonTransaction;
import com.finance.personal.transaction.PersonTransactionRepo;
import com.finance.personal.transaction.PersonTransactionService;
import com.finance.personal.transaction.PersonTransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DepositProfitService {

    private final PersonBalanceRepo personBalanceRepo;
    private final PersonBalanceService personBalanceService;
    private final PersonTransactionService personTransactionService;
    private final PersonTransactionRepo personTransactionRepo;
    private final FxModule fxModule;
    private final TransactionTemplate transactionTemplate;

    public DepositProfitService(
        PlatformTransactionManager transactionManager,
        PersonBalanceRepo personBalanceRepo,
        PersonBalanceService personBalanceService,
        PersonTransactionService personTransactionService,
        PersonTransactionRepo personTransactionRepo,
        FxModule fxModule
    ) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.personBalanceRepo = personBalanceRepo;
        this.personBalanceService = personBalanceService;
        this.personTransactionService = personTransactionService;
        this.personTransactionRepo = personTransactionRepo;
        this.fxModule = fxModule;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void run() {
        applyMonthlyDepositProfits(false);
    }

    public int applyMonthlyDepositProfits(boolean force) {
        log.info("Starting deposit monthly profit application job (force={})", force);
        List<PersonBalance> deposits = personBalanceRepo.findByType(PersonBalanceType.DEPOSIT);
        int appliedCount = 0;

        for (PersonBalance deposit : deposits) {
            if (isProfitDue(deposit, force)) {
                applyProfitForDeposit(deposit);
                appliedCount++;
            }
        }

        log.info("Completed deposit monthly profit application job. Applied profit to {} deposits", appliedCount);
        return appliedCount;
    }

    public int applyMonthlyDepositProfitsForPerson(Long personId, boolean force) {
        log.info("Applying deposit profit for personId={} (force={})", personId, force);
        List<PersonBalance> deposits = personBalanceRepo.getPersonalBalancesByPersonIdAndType(personId, PersonBalanceType.DEPOSIT);
        int appliedCount = 0;

        for (PersonBalance deposit : deposits) {
            if (isProfitDue(deposit, force)) {
                applyProfitForDeposit(deposit);
                appliedCount++;
            }
        }

        log.info("Completed deposit profit for personId={}. Applied to {} deposits", personId, appliedCount);
        return appliedCount;
    }

    private boolean isProfitDue(PersonBalance balance, boolean force) {
        if (balance.getAmount() == null || balance.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (balance.getInterestRate() == null || balance.getInterestRate().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (force) {
            return true;
        }

        LocalDate lastProfitDate = balance.getLastProfitDate();
        if (lastProfitDate == null) {
            return true;
        }

        return !lastProfitDate.plusMonths(1).isAfter(LocalDate.now());
    }

    private void applyProfitForDeposit(PersonBalance deposit) {
        transactionTemplate.execute(status -> {
            BigDecimal yearlyRate = deposit.getInterestRate().divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
            BigDecimal monthlyProfit = deposit.getAmount().multiply(yearlyRate)
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

            if (monthlyProfit.compareTo(BigDecimal.ZERO) <= 0) {
                return null;
            }

            personTransactionService.createAndSavePersonTransaction(
                deposit.getPersonId(),
                deposit.getId(),
                monthlyProfit,
                PersonTransactionType.PROFIT,
                PersonProfitType.DEPOSIT,
                null,
                "Monthly deposit interest (" + deposit.getInterestRate() + "% yearly)",
                LocalDate.now()
            );

            personBalanceService.update(
                deposit.getId(),
                monthlyProfit,
                false
            );

            deposit.setLastProfitDate(LocalDate.now());
            personBalanceRepo.save(deposit);
            return null;
        });
    }

    public BigDecimal calcOverallDepositProfit(Long personId, Long baseCurrencyId) {
        List<PersonTransaction> depositProfits = personTransactionRepo.findByPersonIdAndTypeAndProfitType(
                personId,
                PersonTransactionType.PROFIT,
                PersonProfitType.DEPOSIT
        );

        if (depositProfits.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        if (baseCurrencyId == null) {
            BigDecimal total = depositProfits.stream()
                    .map(PersonTransaction::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return total.setScale(2, RoundingMode.HALF_UP);
        }

        var forexRates = fxModule.getForexRates(baseCurrencyId);
        var cryptoRates = fxModule.getCryptoRates(baseCurrencyId);
        var allRates = new java.util.ArrayList<com.finance.fx.FxModuleRateDto>();
        if (forexRates != null) allRates.addAll(forexRates);
        if (cryptoRates != null) allRates.addAll(cryptoRates);

        var balances = personBalanceRepo.getPersonalBalancesByPersonId(personId);
        var balanceCurrencyMap = balances.stream()
                .filter(b -> b.getId() != null && b.getCurrencyId() != null)
                .collect(Collectors.toMap(PersonBalance::getId, PersonBalance::getCurrencyId, (a, b) -> a));

        BigDecimal total = BigDecimal.ZERO;
        for (PersonTransaction tx : depositProfits) {
            if (tx.getAmount() == null) continue;
            Long currencyId = balanceCurrencyMap.get(tx.getBalanceId());
            if (currencyId == null || currencyId.equals(baseCurrencyId)) {
                total = total.add(tx.getAmount());
                continue;
            }

            var direct = allRates.stream()
                    .filter(r -> r.baseCurrencyId().equals(currencyId) && r.quoteCurrencyId().equals(baseCurrencyId))
                    .findFirst();
            if (direct.isPresent()) {
                total = total.add(tx.getAmount().multiply(direct.get().price()));
                continue;
            }

            var inverse = allRates.stream()
                    .filter(r -> r.baseCurrencyId().equals(baseCurrencyId) && r.quoteCurrencyId().equals(currencyId))
                    .findFirst();
            if (inverse.isPresent() && inverse.get().price().compareTo(BigDecimal.ZERO) != 0) {
                total = total.add(tx.getAmount().divide(inverse.get().price(), 8, RoundingMode.HALF_UP));
                continue;
            }

            total = total.add(tx.getAmount());
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}

