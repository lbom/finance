package com.finance.personal.balance;

import com.finance.fx.FxModule;
import com.finance.fx.FxModuleRateDto;
import com.finance.fx.pair.Pair;
import com.finance.fx.pair.PairRepo;
import com.finance.fx.pair.PairType;
import com.finance.fx.rate.FxCandleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonBalanceServiceTest {

    @Mock
    private PersonBalanceRepo repo;

    @Mock
    private FxModule fxModule;

    @Mock
    private PairRepo pairRepo;

    private PersonBalanceService service;

    private static final Long PERSON_ID = 1L;
    private static final Long USDT_ID = 9L;
    private static final Long BTC_ID = 12L;
    private static final Long USD_ID = 8L;
    private static final Long EUR_ID = 5L;

    @BeforeEach
    void setUp() {
        service = new PersonBalanceService(repo, fxModule, pairRepo);
    }

    @Test
    void sumAll_cryptoHoldings_convertsBtcAndSumsToUsdt() {
        Pair btcUsdtPair = new Pair();
        btcUsdtPair.setId(5L);
        btcUsdtPair.setBaseCurrencyId(BTC_ID);
        btcUsdtPair.setQuoteCurrencyId(USDT_ID);
        btcUsdtPair.setType(PairType.CRYPTO);
        btcUsdtPair.setSymbol("BTCUSDT");

        when(pairRepo.findAllByType(PairType.CRYPTO)).thenReturn(List.of(btcUsdtPair));

        PersonBalance btcBalance = new PersonBalance();
        btcBalance.setId(14L);
        btcBalance.setPersonId(PERSON_ID);
        btcBalance.setCurrencyId(BTC_ID);
        btcBalance.setAmount(new BigDecimal("0.006198"));
        btcBalance.setType(PersonBalanceType.CRYPTO);

        PersonBalance usdtBalance = new PersonBalance();
        usdtBalance.setId(6L);
        usdtBalance.setPersonId(PERSON_ID);
        usdtBalance.setCurrencyId(USDT_ID);
        usdtBalance.setAmount(new BigDecimal("20.00"));
        usdtBalance.setType(PersonBalanceType.CRYPTO);

        when(repo.getPersonalBalancesByPersonId(PERSON_ID)).thenReturn(List.of(btcBalance, usdtBalance));

        FxModuleRateDto btcRate = new FxModuleRateDto(
                5L,
                BTC_ID,
                USDT_ID,
                "BTCUSDT",
                FxCandleType.D,
                LocalDate.now(),
                new BigDecimal("82873.03")
        );
        when(fxModule.getCryptoRates(USDT_ID)).thenReturn(List.of(btcRate));

        BigDecimal total = service.sumAll(PERSON_ID, USDT_ID, PersonBalanceType.CRYPTO);

        // 0.006198 * 82873.03 = 513.64703994
        // + 20.00 = 533.64703994 -> 533.65
        assertThat(total).isEqualByComparingTo(new BigDecimal("533.65"));
    }

    @Test
    void sumAll_forexHoldings_dividesByRate() {
        Pair usdEurPair = new Pair();
        usdEurPair.setId(1L);
        usdEurPair.setBaseCurrencyId(USD_ID);
        usdEurPair.setQuoteCurrencyId(EUR_ID);
        usdEurPair.setType(PairType.FOREX);
        usdEurPair.setSymbol("USDEUR");

        when(pairRepo.findAllByType(PairType.CRYPTO)).thenReturn(List.of());

        PersonBalance eurBalance = new PersonBalance();
        eurBalance.setId(1L);
        eurBalance.setPersonId(PERSON_ID);
        eurBalance.setCurrencyId(EUR_ID);
        eurBalance.setAmount(new BigDecimal("1000.00"));
        eurBalance.setType(PersonBalanceType.REGULAR);

        when(repo.getPersonalBalancesByPersonId(PERSON_ID)).thenReturn(List.of(eurBalance));

        FxModuleRateDto eurRate = new FxModuleRateDto(
                1L,
                USD_ID,
                EUR_ID,
                "USDEUR",
                FxCandleType.D,
                LocalDate.now(),
                new BigDecimal("0.89")
        );
        when(fxModule.getForexRates(USD_ID)).thenReturn(List.of(eurRate));

        BigDecimal total = service.sumAll(PERSON_ID, USD_ID, PersonBalanceType.REGULAR);

        // 1000.00 / 0.89 = 1123.59550562 -> 1123.60
        assertThat(total).isEqualByComparingTo(new BigDecimal("1123.60"));
    }

    @Test
    void sumAll_emptyBalances_returnsZero() {
        when(pairRepo.findAllByType(PairType.CRYPTO)).thenReturn(List.of());
        when(repo.getPersonalBalancesByPersonId(PERSON_ID)).thenReturn(List.of());

        BigDecimal total = service.sumAll(PERSON_ID, USDT_ID, PersonBalanceType.CRYPTO);

        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void save_autoAssignsCryptoType_forCryptoCurrency() {
        Pair btcUsdtPair = new Pair();
        btcUsdtPair.setBaseCurrencyId(BTC_ID);
        btcUsdtPair.setQuoteCurrencyId(USDT_ID);
        btcUsdtPair.setType(PairType.CRYPTO);

        when(pairRepo.findAllByType(PairType.CRYPTO)).thenReturn(List.of(btcUsdtPair));

        PersonBalance balance = new PersonBalance();
        balance.setCurrencyId(BTC_ID);
        balance.setAmount(new BigDecimal("0.006198"));
        balance.setType(PersonBalanceType.REGULAR);

        service.save(balance);

        assertThat(balance.getType()).isEqualTo(PersonBalanceType.CRYPTO);
        verify(repo).save(balance);
    }
}
