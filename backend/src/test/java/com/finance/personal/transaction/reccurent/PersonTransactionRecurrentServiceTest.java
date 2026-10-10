package com.finance.personal.transaction.reccurent;

import com.finance.personal.balance.PersonBalanceService;
import com.finance.personal.transaction.PersonProfitType;
import com.finance.personal.transaction.PersonTransactionService;
import com.finance.personal.transaction.PersonTransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonTransactionRecurrentServiceTest {

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private PersonBalanceService personBalanceService;

    @Mock
    private PersonTransactionService personTransactionService;

    @Mock
    private PersonTransactionRecurrentRepo personTransactionRecurrentRepo;

    @Mock
    private TransactionStatus transactionStatus;

    private PersonTransactionRecurrentService service;

    @BeforeEach
    void setUp() {
        lenient().when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        service = new PersonTransactionRecurrentService(
                transactionManager,
                personBalanceService,
                personTransactionService,
                personTransactionRecurrentRepo
        );
    }

    @Test
    void processSalaryTransactions_executesAndUpdatesBalanceAndLastRunDate() {
        PersonTransactionRecurrent salaryRecurrent = new PersonTransactionRecurrent();
        salaryRecurrent.setId(100L);
        salaryRecurrent.setPersonId(1L);
        salaryRecurrent.setBalanceId(5L);
        salaryRecurrent.setName("Monthly Job Salary");
        salaryRecurrent.setAmount(new BigDecimal("5000.00"));
        salaryRecurrent.setType(PersonTransactionType.PROFIT);
        salaryRecurrent.setProfitType(PersonProfitType.SALARY);
        salaryRecurrent.setPeriodDays(30);
        salaryRecurrent.setIsActive(true);
        salaryRecurrent.setLastRunDate(LocalDateTime.now().minusDays(31));

        when(personTransactionRecurrentRepo.getPersonTransactionRecurrentByIsActiveTrue())
                .thenReturn(List.of(salaryRecurrent));

        int processed = service.processSalaryTransactions(false);

        assertThat(processed).isEqualTo(1);

        verify(personTransactionService).createAndSavePersonTransaction(
                eq(1L),
                eq(5L),
                eq(new BigDecimal("5000.00")),
                eq(PersonTransactionType.PROFIT),
                eq(PersonProfitType.SALARY),
                isNull(),
                eq("Monthly Job Salary"),
                eq(java.time.LocalDate.now())
        );

        verify(personBalanceService).update(eq(5L), eq(new BigDecimal("5000.00")), eq(false));

        assertThat(salaryRecurrent.getLastRunDate().toLocalDate()).isEqualTo(java.time.LocalDate.now());
        verify(personTransactionRecurrentRepo).save(salaryRecurrent);
    }

    @Test
    void processAllRecurrentTransactions_skipsIfNotDue() {
        PersonTransactionRecurrent salaryRecurrent = new PersonTransactionRecurrent();
        salaryRecurrent.setId(100L);
        salaryRecurrent.setPeriodDays(30);
        salaryRecurrent.setIsActive(true);
        salaryRecurrent.setLastRunDate(LocalDateTime.now().minusDays(5)); // only 5 days ago

        when(personTransactionRecurrentRepo.getPersonTransactionRecurrentByIsActiveTrue())
                .thenReturn(List.of(salaryRecurrent));

        int processed = service.processAllRecurrentTransactions(false);

        assertThat(processed).isEqualTo(0);
        verifyNoInteractions(personTransactionService);
    }

    @Test
    void processRecurrentTransactions_executesWhenForceIsTrue() {
        PersonTransactionRecurrent salaryRecurrent = new PersonTransactionRecurrent();
        salaryRecurrent.setId(100L);
        salaryRecurrent.setPersonId(1L);
        salaryRecurrent.setBalanceId(5L);
        salaryRecurrent.setName("Monthly Job Salary");
        salaryRecurrent.setAmount(new BigDecimal("5000.00"));
        salaryRecurrent.setType(PersonTransactionType.PROFIT);
        salaryRecurrent.setProfitType(PersonProfitType.SALARY);
        salaryRecurrent.setPeriodDays(30);
        salaryRecurrent.setIsActive(true);
        salaryRecurrent.setLastRunDate(LocalDateTime.now()); // Run today

        when(personTransactionRecurrentRepo.getPersonTransactionRecurrentByPersonIdAndIsActiveTrue(1L))
                .thenReturn(List.of(salaryRecurrent));

        int processed = service.processRecurrentTransactionsForPerson(1L, true);

        assertThat(processed).isEqualTo(1);
        verify(personTransactionService).createAndSavePersonTransaction(
                eq(1L),
                eq(5L),
                eq(new BigDecimal("5000.00")),
                eq(PersonTransactionType.PROFIT),
                eq(PersonProfitType.SALARY),
                isNull(),
                eq("Monthly Job Salary"),
                eq(java.time.LocalDate.now())
        );
    }
}

