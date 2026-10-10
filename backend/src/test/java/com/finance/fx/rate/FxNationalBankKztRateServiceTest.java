package com.finance.fx.rate;

import com.finance.fx.pair.Pair;
import com.finance.fx.pair.PairRepo;
import com.finance.fx.pair.PairType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FxNationalBankKztRateServiceTest {

    @Mock
    private PairRepo pairRepo;

    @Mock
    private FxRateRepo fxRateRepo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FxNationalBankKztRateService service;

    private static final String SAMPLE_XML = """
            <?xml version="1.0" encoding="utf-8"?>
            <rss version="2.0">
                <channel>
                    <title>Official exchange rates</title>
                    <item>
                        <title>USD</title>
                        <pubDate>10.10.2026</pubDate>
                        <description>453.93</description>
                        <quant>1</quant>
                    </item>
                    <item>
                        <title>AMD</title>
                        <pubDate>10.10.2026</pubDate>
                        <description>12.59</description>
                        <quant>10</quant>
                    </item>
                </channel>
            </rss>
            """;

    @Test
    void parseRatesXml_parsesRatesAndQuantCorrectly() {
        Map<String, FxNationalBankKztRateService.KztRate> map = service.parseRatesXml(SAMPLE_XML);

        assertThat(map).containsKey("USDKZT");
        FxNationalBankKztRateService.KztRate usdRate = map.get("USDKZT");
        assertThat(usdRate.price()).isEqualByComparingTo(new BigDecimal("453.93"));
        assertThat(usdRate.date()).isEqualTo(LocalDate.of(2026, 10, 10));

        assertThat(map).containsKey("AMDKZT");
        FxNationalBankKztRateService.KztRate amdRate = map.get("AMDKZT");
        // 12.59 / 10 = 1.2590
        assertThat(amdRate.price()).isEqualByComparingTo(new BigDecimal("1.2590"));
    }

    @Test
    void refreshRates_fetchesAndSavesMatchedForexPairs() {
        service.setRestTemplate(restTemplate);
        ReflectionTestUtils.setField(service, "url", "https://nationalbank.kz/rss/rates_all.xml");

        Pair pair = new Pair();
        pair.setId(3L);
        pair.setSymbol("USDKZT");
        pair.setType(PairType.FOREX);
        pair.setBaseCurrencyId(8L);
        pair.setQuoteCurrencyId(11L);

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(SAMPLE_XML);
        when(pairRepo.findAllByType(PairType.FOREX)).thenReturn(List.of(pair));

        service.refreshRates();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<FxRate>> captor = ArgumentCaptor.forClass(List.class);
        verify(fxRateRepo).saveAll(captor.capture());

        List<FxRate> savedRates = captor.getValue();
        assertThat(savedRates).hasSize(1);
        FxRate saved = savedRates.getFirst();
        assertThat(saved.getPairId()).isEqualTo(3L);
        assertThat(saved.getPrice()).isEqualByComparingTo(new BigDecimal("453.93"));
        assertThat(saved.getCandleDate()).isEqualTo(LocalDate.of(2026, 10, 10));
        assertThat(saved.getCandleType()).isEqualTo(FxCandleType.D);
    }
}
