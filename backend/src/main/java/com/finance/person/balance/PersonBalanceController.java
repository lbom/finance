package com.finance.person.balance;

import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/person/balance/{personId}")
@RequiredArgsConstructor
public class PersonBalanceController {

    private final PersonBalanceService service;
    private final PersonBalanceMapper mapper;
    private final UserModule userModule;

    @GetMapping
    public List<PersonBalanceDto> getBalances(
        @PathVariable @RequestParam Long personId
    ) {
        userModule.hasAuthority(personId);
        var balances = service.getBalances(personId);
        return mapper.toDto(balances);
    }

    @PostMapping
    public void add(
        @PathVariable @RequestParam Long personId,
        @Validated @RequestBody PersonBalanceDto dto)
    {
        userModule.hasAuthority(personId);
        var balance = mapper.toEntity(dto);
        service.save(balance);
    }
}