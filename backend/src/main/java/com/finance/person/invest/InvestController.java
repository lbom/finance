package com.finance.person.invest;

import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/finance/person/invest/{personId}")
public class InvestController {

    private final InvestService investService;
    private final InvestMapper investMapper;
    private final UserModule userModule;

    @GetMapping
    public List<InvestDto> getList(@PathVariable @RequestParam Long personId) {
        userModule.hasAuthority(personId);
        var invests = investService.getPersonInvests(personId);
        return investMapper.toDto(invests);
    }

    @PostMapping
    public void addInvest(
        @PathVariable @RequestParam Long personId,
        @RequestBody InvestDto investDto
    ) {
        userModule.hasAuthority(personId);
        var invest = investMapper.toEntity(investDto);
        investService.addInvest(invest);
    }

    @GetMapping("/profit")
    public BigDecimal getProfit(@PathVariable @RequestParam Long personId) {
        userModule.hasAuthority(personId);
        var invests = investService.getPersonInvests(personId);
        return investService.calcProfit(invests);
    }
}