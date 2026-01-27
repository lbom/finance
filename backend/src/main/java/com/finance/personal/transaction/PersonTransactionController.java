package com.finance.personal.transaction;

import com.finance.app.CheckPersonAuthority;
import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/finance/personal/transaction")
@RequiredArgsConstructor
public class PersonTransactionController {

    private final PersonTransactionService service;
    private final PersonTransactionMapper mapper;
    private final UserModule userModule;

    @GetMapping
    @CheckPersonAuthority
    public List<PersonTransactionDto> getTransactionsListByType(
        @RequestParam Long personId,
        @RequestParam PersonTransactionType type
    ) {
        userModule.hasAuthority(personId);
        var transactions = service.getTransactionsByType(personId, type);
        return mapper.toDto(transactions);
    }

    @PostMapping
    @CheckPersonAuthority
    public void add(
        @RequestParam Long personId,
        @Validated  @RequestBody PersonTransactionDto dto
    ) {
        userModule.hasAuthority(personId);
        var transaction = mapper.toEntity(dto);
        service.save(transaction);
    }

    @DeleteMapping("/{transactionId}")
    @CheckPersonAuthority
    public void delete(
        @RequestParam Long personId,
        @PathVariable Long transactionId
    ) {
        userModule.hasAuthority(personId);
        service.deleteTransaction(personId, transactionId);
    }
}