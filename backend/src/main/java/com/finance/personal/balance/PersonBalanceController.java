package com.finance.personal.balance;

import com.finance.app.CheckPersonAuthority;
import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/finance/personal/balance")
@RequiredArgsConstructor
public class PersonBalanceController {

    private final PersonBalanceService service;
    private final PersonBalanceMapper mapper;
    private final UserModule userModule;
    private final DepositProfitService depositProfitService;

    @PostMapping("/apply-deposit-profits")
    @CheckPersonAuthority
    public java.util.Map<String, Object> applyDepositProfits(
        @RequestParam Long personId,
        @RequestParam(required = false, defaultValue = "false") boolean force
    ) {
        int applied = depositProfitService.applyMonthlyDepositProfitsForPerson(personId, force);
        return java.util.Map.of("applied", applied);
    }

    @GetMapping("/deposit-profit")
    @CheckPersonAuthority
    public BigDecimal getDepositProfit(
        @RequestParam Long personId,
        @RequestParam(required = false) Long baseCurrencyId
    ) {
        return depositProfitService.calcOverallDepositProfit(personId, baseCurrencyId);
    }

    @GetMapping
    @CheckPersonAuthority
    public List<PersonBalanceDto> getBalances(
        @RequestParam Long personId
    ) {
        var balances = service.getBalances(personId);
        return mapper.toDto(balances);
    }

    @PostMapping
    @CheckPersonAuthority
    public void add(
        @RequestParam Long personId,
        @Validated @RequestBody PersonBalanceDto dto)
    {
        var balance = mapper.toEntity(dto);
        service.save(balance);
    }

    @GetMapping("/sumAll")
    @CheckPersonAuthority
    public BigDecimal getSumAll(
        @RequestParam Long personId,
        @RequestParam Long baseCurrencyId,
        @RequestParam PersonBalanceType balanceType
    ) {
        return service.sumAll(personId, baseCurrencyId, balanceType);
    }

    @DeleteMapping("/{balanceId}")
    @CheckPersonAuthority
    public void delete(
        @RequestParam Long personId,
        @PathVariable Long balanceId
    ) {
        service.deleteBalance(personId, balanceId);
    }
}