package com.finance.personal.transaction.reccurent;

import com.finance.app.CheckPersonAuthority;
import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/finance/personal/transaction/recurrent")
@RequiredArgsConstructor
public class PersonTransactionRecurrentController {

    private final PersonTransactionRecurrentService service;
    private final PersonTransactionRecurrentMapper mapper;

    @GetMapping
    @CheckPersonAuthority
    public List<PersonTransactionRecurrentDto> getTransactionsListByType(
        @RequestParam Long personId
    ) {
        var transactions = service.getTransactionsByPersonId(personId);
        return mapper.toDto(transactions);
    }

    @GetMapping("/sumSubscriptions")
    @CheckPersonAuthority
    public BigDecimal getSubscriptionsSumAverageMonthly(
        @RequestParam Long personId
    ) {
        return service.sumActiveSubscriptionsAverageMonthly(personId);
    }

    @PostMapping
    @CheckPersonAuthority
    public void add(
        @RequestParam Long personId,
        @Validated @RequestBody PersonTransactionRecurrentDto dto
    ) {
        var transaction = mapper.toEntity(dto);
        service.save(transaction);
    }

    @PutMapping
    @CheckPersonAuthority
    public void update(
        @RequestParam Long personId,
        @Validated @RequestBody PersonTransactionRecurrentDto dto
    ) {
        var transaction = mapper.toEntity(dto);
        service.save(transaction);
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
