package com.finance.person.transaction.reccurent;

import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/person/transaction/recurrent/{personId}")
@RequiredArgsConstructor
public class PersonTransactionRecurrentController {

    private final PersonTransactionRecurrentService service;
    private final PersonTransactionRecurrentMapper mapper;
    private final UserModule userModule;

    @GetMapping
    public List<PersonTransactionRecurrentDto> getTransactionsListByType(
        @PathVariable @RequestParam Long personId
    ) {
        userModule.hasAuthority(personId);
        var transactions = service.getTransactionsByPersonId(personId);
        return mapper.toDto(transactions);
    }

    @PostMapping
    public void add(
        @PathVariable @RequestParam Long personId,
        @Validated @RequestBody PersonTransactionRecurrentDto dto
    ) {
        userModule.hasAuthority(personId);
        var transaction = mapper.toEntity(dto);
        service.save(transaction);
    }

    @PutMapping
    public void update(
        @PathVariable @RequestParam Long personId,
        @Validated @RequestBody PersonTransactionRecurrentDto dto
    ) {
        userModule.hasAuthority(personId);
        var transaction = mapper.toEntity(dto);
        service.save(transaction);
    }
}