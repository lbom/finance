package com.finance.personal.transaction;

import com.finance.app.CheckPersonAuthority;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/finance/personal/transaction")
@RequiredArgsConstructor
public class PersonTransactionController {

    private final PersonTransactionService service;
    private final PersonTransactionMapper mapper;

    @GetMapping
    @CheckPersonAuthority
    public List<PersonTransactionDto> getTransactionsListByType(
        @RequestParam Long personId,
        @RequestParam PersonTransactionType type
    ) {
        var transactions = service.getTransactionsByType(personId, type);
        return mapper.toDto(transactions);
    }

    @GetMapping("/monthlyExpenses")
    @CheckPersonAuthority
    public BigDecimal getMonthlyExpenses(
        @RequestParam Long personId,
        @RequestParam(required = false) PersonSpendingType spendingType,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month
    ) {
        return service.getMonthlyExpenses(personId, spendingType, year, month);
    }

    @PostMapping
    @CheckPersonAuthority
    public void add(
        @RequestParam Long personId,
        @Validated  @RequestBody PersonTransactionDto dto
    ) {
        var transaction = mapper.toEntity(dto);
        service.save(transaction);
    }

    @PutMapping("/{transactionId}")
    @CheckPersonAuthority
    public void update(
        @RequestParam Long personId,
        @PathVariable Long transactionId,
        @Validated @RequestBody PersonTransactionDto dto
    ) {
        service.updateTransaction(personId, transactionId, dto);
    }

    @DeleteMapping("/{transactionId}")
    @CheckPersonAuthority
    public void delete(
        @RequestParam Long personId,
        @PathVariable Long transactionId
    ) {
        service.deleteTransaction(personId, transactionId);
    }
}