package com.finance.person.trade;

import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/finance/person/trades/{personId}")
public class TradeController {

    private final TradeService tradeService;
    private final TradeMapper tradeMapper;
    private final UserModule userModule;

    @GetMapping
    public List<TradeDto> getList(
        @PathVariable @RequestParam Long personId
    ) {
        userModule.hasAuthority(personId);
        var trades = tradeService.getTrades(personId);
        return tradeMapper.toDto(trades);
    }

    @PostMapping
    public void addTrade(
        @PathVariable @RequestParam Long personId,
        @RequestBody TradeDto tradeDto
    ) {
        userModule.hasAuthority(personId);
        var trades = tradeMapper.toEntity(tradeDto);
        tradeService.addTrade(trades);
    }

    @GetMapping("/profit")
    public BigDecimal getProfit(@PathVariable @RequestParam Long personId) {
        userModule.hasAuthority(personId);
        var trades = tradeService.getTrades(personId);
        return tradeService.calcProfit(trades);
    }
}