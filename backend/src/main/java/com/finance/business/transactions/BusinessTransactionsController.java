package com.finance.business.transactions;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/business/transaction")
@RequiredArgsConstructor
public class BusinessTransactionsController {

    private final BusinessTransactionsService service;
    private final BusinessTransactionsMapper mapper;

    @GetMapping
    public List<BusinessTransactionDto> getTransactionsByType(
        @RequestParam BusinessTransactionType type
    ) {
        var transactions = service.getTransactionsByType(type);
        return mapper.toDto(transactions);
    }

    @PostMapping
    public void add(@RequestBody BusinessTransactionDto dto) {
        var transaction = mapper.toEntity(dto);
        service.save(transaction);
    }
}