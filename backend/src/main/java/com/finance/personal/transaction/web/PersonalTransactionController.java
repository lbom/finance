package com.finance.personal.transaction.web;

import com.finance.personal.transaction.persistence.PersonalTransactionType;
import com.finance.personal.transaction.service.PersonalTransactionService;
import com.finance.personal.transaction.web.mapper.PersonalTransactionDto;
import com.finance.personal.transaction.web.mapper.PersonalTransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/finance/personal/transaction")
@RequiredArgsConstructor
public class PersonalTransactionController {

    private final PersonalTransactionService service;
    private final PersonalTransactionMapper mapper;

    @GetMapping
    public List<PersonalTransactionDto> getTransactionsListByType(
        @RequestParam PersonalTransactionType type
    ) {
        return mapper.toDto(service.getTransactionsByType(type));
    }

    @PostMapping
    public void add(@RequestBody PersonalTransactionDto dto) {
        service.save(mapper.toEntity(dto));
    }
}