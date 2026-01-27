package com.finance.fx.rate;

import com.finance.fx.pair.Pair;
import com.finance.fx.pair.PairRepo;
import com.finance.fx.pair.PairType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FxFrankfurterRateService {

    private final PairRepo pairRepo;
    private final FxRateRepo fxRateRepo;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fx.base-url}")
    private String baseUrl;

    @Value("${fx.base-currency:USD}")
    private String baseCurrency;

    @Scheduled(cron = "0 0 0,12 * * *")
    public void refreshRates() {
        FxFrankfurterRateResponse response = fetchRates();
        Map<String, BigDecimal> ratesMap = response.rates().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> response.base() + e.getKey(),
                        Map.Entry::getValue
                ));
        List<Pair> forexPairs = pairRepo.findAllByType(PairType.FOREX);
        List<FxRate> updatedRates = forexPairs.stream()
                .filter(pair -> ratesMap.containsKey(pair.getSymbol()))
                .map(pair -> createFxRate(pair, ratesMap.get(pair.getSymbol()), response.date()))
                .toList();
        if (!updatedRates.isEmpty()) {
            fxRateRepo.saveAll(updatedRates);
        }
    }

    private FxFrankfurterRateResponse fetchRates() {
        String url = String.format("%s?base=%s", baseUrl, baseCurrency);
        FxFrankfurterRateResponse response = restTemplate.getForObject(url, FxFrankfurterRateResponse.class);
        if (response == null || response.rates() == null) {
            throw new FxProviderException("FX provider returned empty response for: " + baseCurrency);
        }
        return response;
    }

    private FxRate createFxRate(Pair pair, BigDecimal price, LocalDate date) {
        FxRate fxRate = new FxRate();
        fxRate.setPairId(pair.getId());
        fxRate.setPrice(price);
        fxRate.setCandleType(FxCandleType.D);
        fxRate.setCandleDate(date);
        return fxRate;
    }
}
