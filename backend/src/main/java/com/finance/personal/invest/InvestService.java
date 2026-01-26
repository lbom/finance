package com.finance.personal.invest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InvestService {

    private final InvestRepo investRepo;

    public List<Invest> getPersonInvests(Long personId) {
        return investRepo.getInvestsByPersonId(personId);
    }

    public void addInvest(Invest investment) {
        investRepo.save(investment);
    }

    public BigDecimal calcProfit(List<Invest> investments) {
        return investments.stream()
                .map(Invest::getProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}