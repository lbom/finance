package com.finance.invest.service;

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

    private final InvestRepo tradeRepo;

    public List<Invest> getInvests() {
        return tradeRepo.findAll();
    }

    public void addInvest(Invest trade) {
        tradeRepo.save(trade);
    }

    public BigDecimal calcProfit(List<Invest> trades) {
        return trades.stream()
                .map(Invest::getProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}