package com.finance.personal.transaction.web;

import com.finance.app.UserInterface;
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
    private final UserInterface userInterface;

    @GetMapping
    public List<PersonalTransactionDto> getTransactionsListByType(
        @RequestParam PersonalTransactionType type
    ) {
        var transactions = service.getTransactionsByType(type);
        return mapper.toDto(transactions);
    }

    @PostMapping
    public void add(@RequestBody PersonalTransactionDto dto) {
        var transaction = mapper.toEntity(dto);
        var userId = userInterface.retrieveUserId();
        transaction.setUserId(userId);
        service.save(transaction);
    }
}