package com.finance.trades.service;

import com.finance.trades.persistence.Trade;
import com.finance.trades.persistence.TradeRepo;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepo tradeRepo;

    public List<Trade> getTrades() {
        return tradeRepo.findAll();
    }

    public void addTrade(Trade trade) {
        tradeRepo.save(trade);
    }

    public BigDecimal calcProfit(List<Trade> trades) {
        return trades.stream()
                .map(Trade::getProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}