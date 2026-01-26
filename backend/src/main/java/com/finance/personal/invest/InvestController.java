package com.finance.personal.invest;

import com.finance.app.CheckPersonAuthority;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/finance/personal/invest")
public class InvestController {

    private final InvestService investService;
    private final InvestMapper investMapper;

    @GetMapping
    @CheckPersonAuthority
    public List<InvestDto> getList(@RequestParam Long personId) {
        var invests = investService.getPersonInvests(personId);
        return investMapper.toDto(invests);
    }

    @PostMapping
    @CheckPersonAuthority
    public void addInvest(
        @RequestParam Long personId,
        @Validated @RequestBody InvestDto investDto
    ) {
        var invest = investMapper.toEntity(investDto);
        investService.addInvest(invest);
    }

    @GetMapping("/profit")
    @CheckPersonAuthority
    public BigDecimal getProfit(@RequestParam Long personId) {
        var invests = investService.getPersonInvests(personId);
        return investService.calcProfit(invests);
    }
}