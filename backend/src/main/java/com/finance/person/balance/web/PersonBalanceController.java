package com.finance.person.balance.web;

import com.finance.app.UserInterface;
import com.finance.person.balance.service.PersonBalanceService;
import com.finance.person.balance.web.mapper.PersonBalanceDto;
import com.finance.person.balance.web.mapper.PersonBalanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/person/balance")
@RequiredArgsConstructor
public class PersonBalanceController {

    private final PersonBalanceService service;
    private final PersonBalanceMapper mapper;
    private final UserInterface userInterface;

    @GetMapping
    public List<PersonBalanceDto> getBalances() {
        var transactions = service.getBalances();
        return mapper.toDto(transactions);
    }

    @PostMapping
    public void add(@RequestBody PersonBalanceDto dto) {
        var balance = mapper.toEntity(dto);
        var userId = userInterface.retrieveUserId();
        balance.setPersonId(userId);
        service.save(balance);
    }
}