package com.finance.person.transaction.web;

import com.finance.person.transaction.persistence.PersonTransactionType;
import com.finance.person.transaction.service.PersonTransactionService;
import com.finance.person.transaction.web.mapper.PersonTransactionDto;
import com.finance.person.transaction.web.mapper.PersonTransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/finance/person/transaction")
@RequiredArgsConstructor
public class PersonTransactionController {

    private final PersonTransactionService service;
    private final PersonTransactionMapper mapper;

    @GetMapping
    public List<PersonTransactionDto> getTransactionsListByType(
        @RequestParam PersonTransactionType type
    ) {
        var transactions = service.getTransactionsByType(type);
        return mapper.toDto(transactions);
    }

    @PostMapping
    public void add(@RequestBody PersonTransactionDto dto) {
        var transaction = mapper.toEntity(dto);
        service.save(transaction);
    }
}