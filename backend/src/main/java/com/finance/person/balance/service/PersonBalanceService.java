package com.finance.person.balance.service;

import com.finance.app.UserInterface;
import com.finance.person.balance.persistence.PersonBalance;
import com.finance.person.balance.persistence.PersonBalanceRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonBalanceService {

    private final UserInterface userInterface;
    private final PersonBalanceRepo repo;

    public List<PersonBalance> getBalances() {
        var userId = userInterface.retrieveUserId();
        return repo.getPersonalBalancesByPersonId(userId);
    }
    public void save(PersonBalance balance) {
        var userId = userInterface.retrieveUserId();
        balance.setPersonId(userId);
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
