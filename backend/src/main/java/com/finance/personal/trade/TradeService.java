package com.finance.personal.trade;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepo tradeRepo;

    public List<Trade> getTrades(Long personId) {
        return tradeRepo.getTradesByPersonId(personId);
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

    public void updateTrade(Long personId, Long tradeId, TradeDto tradeDto) {
        tradeRepo.findById(tradeId)
            .filter(trade -> trade.getPersonId().equals(personId))
            .ifPresent(trade -> {
                trade.setType(tradeDto.type().name());
                trade.setInstitutionId(tradeDto.institutionId());
                trade.setSymbolId(tradeDto.symbolId());
                trade.setReason(tradeDto.reason());
                trade.setAmount(tradeDto.amount());
                trade.setProfit(tradeDto.profit());
                trade.setStartDate(tradeDto.startDate());
                trade.setEndDate(tradeDto.endDate());
                tradeRepo.save(trade);
            });
    }

    public void deleteTrade(Long personId, Long tradeId) {
        tradeRepo.findById(tradeId)
            .filter(trade -> trade.getPersonId().equals(personId))
            .ifPresent(tradeRepo::delete);
    }
}