package com.finance.invest.service;

import com.finance.app.UserInterface;
import com.finance.invest.persistence.Invest;
import com.finance.invest.persistence.InvestRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InvestService {

    private final UserInterface userInterface;
    private final InvestRepo investRepo;

    public List<Invest> getUserInvests() {
        var userId = userInterface.retrieveUserId();
        return investRepo.getInvestsByUserId(userId);
    }

    public void addInvest(Invest investment) {
        var userId = userInterface.retrieveUserId();
        investment.setUserId(userId);
        investRepo.save(investment);
    }

    public BigDecimal calcProfit(List<Invest> investments) {
        return investments.stream()
                .map(Invest::getProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}