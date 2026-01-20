package com.finance.invest.web;

import com.finance.invest.service.InvestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/finance/invest")
public class InvestController {

    private final InvestService investService;
    private final InvestMapper investMapper;

    @GetMapping
    public List<InvestDto> getList() {
        var invests = investService.getUserInvests();
        return investMapper.toDto(invests);
    }

    @PostMapping
    public void addInvest(@RequestBody InvestDto investDto) {
        var invest = investMapper.toEntity(investDto);
        investService.addInvest(invest);
    }

    @GetMapping("/profit")
    public BigDecimal getProfit() {
        var invests = investService.getUserInvests();
        return investService.calcProfit(invests);
    }
}