package com.finance.person.transaction.reccurent;

import com.finance.app.CheckPersonAuthority;
import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/person/transaction/recurrent")
@RequiredArgsConstructor
public class PersonTransactionRecurrentController {

    private final PersonTransactionRecurrentService service;
    private final PersonTransactionRecurrentMapper mapper;
    private final UserModule userModule;

    @GetMapping
    @CheckPersonAuthority
    public List<PersonTransactionRecurrentDto> getTransactionsListByType(
        @RequestParam Long personId
    ) {
        userModule.hasAuthority(personId);
        var transactions = service.getTransactionsByPersonId(personId);
        return mapper.toDto(transactions);
    }

    @PostMapping
    @CheckPersonAuthority
    public void add(
        @RequestParam Long personId,
        @Validated @RequestBody PersonTransactionRecurrentDto dto
    ) {
        userModule.hasAuthority(personId);
        var transaction = mapper.toEntity(dto);
        service.save(transaction);
    }

    @PutMapping
    @CheckPersonAuthority
    public void update(
        @RequestParam Long personId,
        @Validated @RequestBody PersonTransactionRecurrentDto dto
    ) {
        userModule.hasAuthority(personId);
        var transaction = mapper.toEntity(dto);
        service.save(transaction);
    }
}