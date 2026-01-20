package com.finance.business.web.transactions;

import com.finance.business.service.BusinessTransactionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/business/transactions")
@RequiredArgsConstructor
public class BusinessTransactionsController {

    private final BusinessTransactionsService service;
    private final BusinessTransactionsMapper mapper;

    @GetMapping
    public List<BusinessTransactionDto> getList() {
        return mapper.toDto(service.getAll());
    }

    @PostMapping
    public void add(@RequestBody BusinessTransactionDto dto) {
        service.save(mapper.toEntity(dto));
    }
}