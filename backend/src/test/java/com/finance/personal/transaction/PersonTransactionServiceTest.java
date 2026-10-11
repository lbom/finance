package com.finance.personal.transaction;

import com.finance.personal.balance.PersonBalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonTransactionServiceTest {

    @Mock
    private PersonTransactionRepo repository;

    @Mock
    private PersonBalanceService personBalanceService;

    private PersonTransactionService service;

    @BeforeEach
    void setUp() {
        service = new PersonTransactionService(repository, personBalanceService);
    }

    @Test
    void getMonthlyExpenses_withSpecificSpendingType_sumsMatchingTransactions() {
        Long personId = 1L;
        LocalDate d1 = LocalDate.of(2026, 10, 5);
        LocalDate d2 = LocalDate.of(2026, 10, 11);

        PersonTransaction t1 = new PersonTransaction();
        t1.setAmount(new BigDecimal("11680.00"));
        t1.setSpendingType(PersonSpendingType.FOOD_STORE);
        t1.setType(PersonTransactionType.SPENDING);
        t1.setLocalDate(d1);

        PersonTransaction t2 = new PersonTransaction();
        t2.setAmount(new BigDecimal("850.50"));
        t2.setSpendingType(PersonSpendingType.FOOD_STORE);
        t2.setType(PersonTransactionType.SPENDING);
        t2.setLocalDate(d2);

        when(repository.findByPersonIdAndTypeAndSpendingTypeAndLocalDateBetween(
                eq(personId),
                eq(PersonTransactionType.SPENDING),
                eq(PersonSpendingType.FOOD_STORE),
                eq(LocalDate.of(2026, 10, 1)),
                eq(LocalDate.of(2026, 10, 31))
        )).thenReturn(List.of(t1, t2));

        BigDecimal result = service.getMonthlyExpenses(personId, PersonSpendingType.FOOD_STORE, 2026, 10);

        assertThat(result).isEqualByComparingTo(new BigDecimal("12530.50"));
    }

    @Test
    void getMonthlyExpenses_emptyTransactions_returnsZero() {
        Long personId = 1L;
        when(repository.findByPersonIdAndTypeAndSpendingTypeAndLocalDateBetween(
                eq(personId),
                eq(PersonTransactionType.SPENDING),
                eq(PersonSpendingType.FOOD_RESTAURANT),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(List.of());

        BigDecimal result = service.getMonthlyExpenses(personId, PersonSpendingType.FOOD_RESTAURANT, 2026, 10);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
