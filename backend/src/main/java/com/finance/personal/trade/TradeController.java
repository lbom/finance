package com.finance.personal.trade;

import com.finance.app.CheckPersonAuthority;
import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/finance/personal/trades")
public class TradeController {

    private final TradeService tradeService;
    private final TradeMapper tradeMapper;
    private final UserModule userModule;

    @GetMapping
    @CheckPersonAuthority
    public List<TradeDto> getList(
        @RequestParam Long personId
    ) {
        userModule.hasAuthority(personId);
        var trades = tradeService.getTrades(personId);
        return tradeMapper.toDto(trades);
    }

    @PostMapping
    @CheckPersonAuthority
    public void addTrade(
        @RequestParam Long personId,
        @Validated @RequestBody TradeDto tradeDto
    ) {
        userModule.hasAuthority(personId);
        var trades = tradeMapper.toEntity(tradeDto);
        tradeService.addTrade(trades);
    }

    @GetMapping("/profit")
    @CheckPersonAuthority
    public BigDecimal getProfit(@RequestParam Long personId) {
        userModule.hasAuthority(personId);
        var trades = tradeService.getTrades(personId);
        return tradeService.calcProfit(trades);
    }
}