package com.finance.personal.balance;

import com.finance.personal.transaction.PersonProfitType;
import com.finance.personal.transaction.PersonTransactionService;
import com.finance.personal.transaction.PersonTransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositProfitServiceTest {

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private PersonBalanceRepo personBalanceRepo;

    @Mock
    private PersonBalanceService personBalanceService;

    @Mock
    private PersonTransactionService personTransactionService;

    @Mock
    private com.finance.personal.transaction.PersonTransactionRepo personTransactionRepo;

    @Mock
    private com.finance.fx.FxModule fxModule;

    @Mock
    private TransactionStatus transactionStatus;

    private DepositProfitService depositProfitService;

    @BeforeEach
    void setUp() {
        lenient().when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        depositProfitService = new DepositProfitService(
                transactionManager,
                personBalanceRepo,
                personBalanceService,
                personTransactionService,
                personTransactionRepo,
                fxModule
        );
    }

    @Test
    void applyMonthlyDepositProfits_calculatesAndAppliesMonthlyInterest() {
        PersonBalance deposit = new PersonBalance();
        deposit.setId(10L);
        deposit.setPersonId(1L);
        deposit.setType(PersonBalanceType.DEPOSIT);
        deposit.setAmount(new BigDecimal("12000.00"));
        deposit.setInterestRate(new BigDecimal("12.00")); // 12% yearly = 1% monthly = 120.00
        deposit.setLastProfitDate(LocalDate.now().minusMonths(2));

        when(personBalanceRepo.findByType(PersonBalanceType.DEPOSIT)).thenReturn(List.of(deposit));

        int applied = depositProfitService.applyMonthlyDepositProfits(false);

        assertThat(applied).isEqualTo(1);

        // Verify transaction creation
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(personTransactionService).createAndSavePersonTransaction(
                eq(1L),
                eq(10L),
                amountCaptor.capture(),
                eq(PersonTransactionType.PROFIT),
                eq(PersonProfitType.DEPOSIT),
                isNull(),
                contains("12.00%"),
                eq(LocalDate.now())
        );
        assertThat(amountCaptor.getValue()).isEqualByComparingTo(new BigDecimal("120.00"));

        // Verify balance update
        verify(personBalanceService).update(eq(10L), eq(new BigDecimal("120.00")), eq(false));

        // Verify lastProfitDate updated on balance
        assertThat(deposit.getLastProfitDate()).isEqualTo(LocalDate.now());
        verify(personBalanceRepo).save(deposit);
    }

    @Test
    void isProfitDue_returnsFalse_ifAlreadyAppliedThisMonth() {
        PersonBalance deposit = new PersonBalance();
        deposit.setId(10L);
        deposit.setPersonId(1L);
        deposit.setType(PersonBalanceType.DEPOSIT);
        deposit.setAmount(new BigDecimal("10000.00"));
        deposit.setInterestRate(new BigDecimal("10.00"));
        deposit.setLastProfitDate(LocalDate.now().minusDays(5)); // Applied 5 days ago

        when(personBalanceRepo.findByType(PersonBalanceType.DEPOSIT)).thenReturn(List.of(deposit));

        int applied = depositProfitService.applyMonthlyDepositProfits(false);

        assertThat(applied).isEqualTo(0);
        verifyNoInteractions(personTransactionService);
    }

    @Test
    void isProfitDue_returnsTrue_whenForceIsTrue() {
        PersonBalance deposit = new PersonBalance();
        deposit.setId(10L);
        deposit.setPersonId(1L);
        deposit.setType(PersonBalanceType.DEPOSIT);
        deposit.setAmount(new BigDecimal("10000.00"));
        deposit.setInterestRate(new BigDecimal("12.00"));
        deposit.setLastProfitDate(LocalDate.now()); // Created today

        when(personBalanceRepo.findByType(PersonBalanceType.DEPOSIT)).thenReturn(List.of(deposit));

        int applied = depositProfitService.applyMonthlyDepositProfits(true);

        assertThat(applied).isEqualTo(1);
        verify(personTransactionService).createAndSavePersonTransaction(
                eq(1L),
                eq(10L),
                any(BigDecimal.class),
                eq(PersonTransactionType.PROFIT),
                eq(PersonProfitType.DEPOSIT),
                isNull(),
                anyString(),
                eq(LocalDate.now())
        );
    }

    @Test
    void calcOverallDepositProfit_returnsConvertedProfitSum() {
        var tx1 = new com.finance.personal.transaction.PersonTransaction();
        tx1.setBalanceId(10L);
        tx1.setAmount(new BigDecimal("100.00")); // USD

        var balance10 = new PersonBalance();
        balance10.setId(10L);
        balance10.setCurrencyId(8L); // USD

        when(personTransactionRepo.findByPersonIdAndTypeAndProfitType(
                1L,
                PersonTransactionType.PROFIT,
                PersonProfitType.DEPOSIT
        )).thenReturn(List.of(tx1));

        when(personBalanceRepo.getPersonalBalancesByPersonId(1L)).thenReturn(List.of(balance10));

        BigDecimal total = depositProfitService.calcOverallDepositProfit(1L, 8L);

        assertThat(total).isEqualByComparingTo(new BigDecimal("100.00"));
    }
}


