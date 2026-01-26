package com.finance.person.balance;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonBalanceService {

    private final PersonBalanceRepo repo;

    public List<PersonBalance> getBalances(Long personId) {
        return repo.getPersonalBalancesByPersonId(personId);
    }
    public void save(PersonBalance balance) {
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
}
