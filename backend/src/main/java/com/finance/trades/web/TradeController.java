package com.finance.trades.web;

import com.finance.trades.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/finance/trades")
public class TradeController {

    private final TradeService tradeService;
    private final TradeMapper tradeMapper;

    @GetMapping
    public List<TradeDto> getList(@RequestParam Long personId) {
        var trades = tradeService.getTrades(personId);
        return tradeMapper.toDto(trades);
    }

    @PostMapping
    public void addTrade(@RequestBody TradeDto tradeDto) {
        var trades = tradeMapper.toEntity(tradeDto);
        tradeService.addTrade(trades);
    }

    @GetMapping("/profit")
    public BigDecimal getProfit(@RequestParam Long personId) {
        var trades = tradeService.getTrades(personId);
        return tradeService.calcProfit(trades);
    }
}