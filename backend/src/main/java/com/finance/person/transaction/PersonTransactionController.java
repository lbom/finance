package com.finance.person.transaction;

import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/finance/person/transaction/{personId}")
@RequiredArgsConstructor
public class PersonTransactionController {

    private final PersonTransactionService service;
    private final PersonTransactionMapper mapper;
    private final UserModule userModule;

    @GetMapping
    public List<PersonTransactionDto> getTransactionsListByType(
        @PathVariable @RequestParam Long personId,
        @RequestParam PersonTransactionType type
    ) {
        userModule.hasAuthority(personId);
        var transactions = service.getTransactionsByType(personId, type);
        return mapper.toDto(transactions);
    }

    @PostMapping
    public void add(
        @PathVariable @RequestParam Long personId,
        @RequestBody PersonTransactionDto dto
    ) {
        userModule.hasAuthority(personId);
        var transaction = mapper.toEntity(dto);
        service.save(transaction);
    }
}