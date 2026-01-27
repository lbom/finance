package com.finance.personal.balance;

import com.finance.fx.FxModule;
import com.finance.fx.FxModuleRateDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonBalanceService {

    private final PersonBalanceRepo repo;
    private final FxModule fxModule;

    public List<PersonBalance> getBalances(Long personId) {
        return repo.getPersonalBalancesByPersonId(personId);
    }
    public void save(PersonBalance balance) {
        if (balance.getType() == null) {
            balance.setType(PersonBalanceType.REGULAR);
        }
        repo.save(balance);
    }

    @Transactional
    public void update(
        Long balanceId,
        BigDecimal amount,
        Boolean isSubtract
    ) {
        var balance = repo.findById(balanceId);
        balance.ifPresent(v -> {
            var newBalance = isSubtract ? v.getAmount().subtract(amount) : v.getAmount().add(amount);
            v.setAmount(newBalance);
            repo.save(v);
        });
    }

    public BigDecimal sumAll(
        Long personId,
        Long baseCurrencyId,
        PersonBalanceType balanceType
    ) {
        var balances = repo.getPersonalBalancesByPersonIdAndType(personId, balanceType);
        if (balances == null || balances.isEmpty()) {
            return BigDecimal.ZERO;
        }

        var rates = balanceType == PersonBalanceType.CRYPTO
            ? fxModule.getCryptoRates(baseCurrencyId)
            : fxModule.getForexRates(baseCurrencyId);

        var rateByQuoteCurrency = rates.stream()
            .collect(Collectors.toMap(
                FxModuleRateDto::quoteCurrencyId,
                FxModuleRateDto::price,
                (a, b) -> a
            ));

        BigDecimal total = BigDecimal.ZERO;
        for (var balance : balances) {
            if (balance.getAmount() == null) continue;

            var currencyId = balance.getCurrencyId();
            if (currencyId == null) continue;

            if (currencyId.equals(baseCurrencyId)) {
                total = total.add(balance.getAmount());
                continue;
            }

            var rate = rateByQuoteCurrency.get(currencyId);
            if (rate == null || rate.compareTo(BigDecimal.ZERO) == 0) continue;

            var baseAmount = balance.getAmount().divide(rate, 8, RoundingMode.HALF_UP);
            total = total.add(baseAmount);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
