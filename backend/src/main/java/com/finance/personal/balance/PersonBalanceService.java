package com.finance.personal.balance;

import com.finance.fx.FxModule;
import com.finance.fx.FxModuleRateDto;
import com.finance.fx.pair.PairRepo;
import com.finance.fx.pair.PairType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PersonBalanceService {

    private final PersonBalanceRepo repo;
    private final FxModule fxModule;
    private final PairRepo pairRepo;

    public List<PersonBalance> getBalances(Long personId) {
        return repo.getPersonalBalancesByPersonId(personId);
    }

    public Optional<PersonBalance> findById(Long id) {
        return repo.findById(id);
    }

    public void save(PersonBalance balance) {
        if (balance.getType() == null) {
            balance.setType(PersonBalanceType.REGULAR);
        }
        if (balance.getCurrencyId() != null && balance.getType() == PersonBalanceType.REGULAR) {
            Set<Long> cryptoCurrencyIds = getCryptoCurrencyIds();
            if (cryptoCurrencyIds.contains(balance.getCurrencyId())) {
                balance.setType(PersonBalanceType.CRYPTO);
            }
        }
        if (balance.getType() == PersonBalanceType.DEPOSIT && balance.getLastProfitDate() == null) {
            balance.setLastProfitDate(java.time.LocalDate.now());
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
        List<PersonBalance> balances = getBalancesForType(personId, balanceType);
        if (balances == null || balances.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        var rates = balanceType == PersonBalanceType.CRYPTO
            ? fxModule.getCryptoRates(baseCurrencyId)
            : fxModule.getForexRates(baseCurrencyId);

        BigDecimal total = BigDecimal.ZERO;
        for (var balance : balances) {
            if (balance.getAmount() == null) continue;

            var currencyId = balance.getCurrencyId();
            if (currencyId == null) continue;

            if (currencyId.equals(baseCurrencyId)) {
                total = total.add(balance.getAmount());
                continue;
            }

            if (rates == null || rates.isEmpty()) continue;

            // Direct rate: balance currency is BASE, target currency is QUOTE (e.g. BTC/USDT)
            var directRate = rates.stream()
                .filter(r -> r.baseCurrencyId().equals(currencyId) && r.quoteCurrencyId().equals(baseCurrencyId))
                .findFirst();

            if (directRate.isPresent()) {
                var baseAmount = balance.getAmount().multiply(directRate.get().price());
                total = total.add(baseAmount);
                continue;
            }

            // Inverse rate: target currency is BASE, balance currency is QUOTE (e.g. USD/EUR)
            var inverseRate = rates.stream()
                .filter(r -> r.baseCurrencyId().equals(baseCurrencyId) && r.quoteCurrencyId().equals(currencyId))
                .findFirst();

            if (inverseRate.isPresent() && inverseRate.get().price().compareTo(BigDecimal.ZERO) != 0) {
                var baseAmount = balance.getAmount().divide(inverseRate.get().price(), 8, RoundingMode.HALF_UP);
                total = total.add(baseAmount);
                continue;
            }
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private List<PersonBalance> getBalancesForType(Long personId, PersonBalanceType balanceType) {
        if (balanceType == PersonBalanceType.CRYPTO) {
            Set<Long> cryptoCurrencyIds = getCryptoCurrencyIds();
            var allBalances = repo.getPersonalBalancesByPersonId(personId);
            return allBalances.stream()
                .filter(b -> b.getType() == PersonBalanceType.CRYPTO
                    || (b.getCurrencyId() != null && cryptoCurrencyIds.contains(b.getCurrencyId())))
                .toList();
        } else if (balanceType == PersonBalanceType.REGULAR) {
            Set<Long> cryptoCurrencyIds = getCryptoCurrencyIds();
            var allBalances = repo.getPersonalBalancesByPersonId(personId);
            return allBalances.stream()
                .filter(b -> (b.getType() == PersonBalanceType.REGULAR || b.getType() == null)
                    && (b.getCurrencyId() == null || !cryptoCurrencyIds.contains(b.getCurrencyId())))
                .toList();
        } else {
            return repo.getPersonalBalancesByPersonIdAndType(personId, balanceType);
        }
    }

    private Set<Long> getCryptoCurrencyIds() {
        var cryptoPairs = pairRepo.findAllByType(PairType.CRYPTO);
        return cryptoPairs.stream()
            .flatMap(p -> Stream.of(p.getBaseCurrencyId(), p.getQuoteCurrencyId()))
            .collect(Collectors.toSet());
    }

    public void deleteBalance(Long personId, Long balanceId) {
        repo.findById(balanceId)
            .filter(balance -> balance.getPersonId().equals(personId))
            .ifPresent(repo::delete);
    }
}
