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
}