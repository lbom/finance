package com.finance.fx.rate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.fx.pair.Pair;
import com.finance.fx.pair.PairRepo;
import com.finance.fx.pair.PairType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FxBinanceRateService {

    @Value("${fx-crypto.base-url:https://api.binance.com/api/v3/klines}")
    private String baseUrl;

    private final FxRateRepo repo;
    private final PairRepo pairRepo;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String KLINE_INTERVAL = "1d";
    private static final int CLOSE_PRICE_INDEX = 4;
    private static final int OPEN_TIME_INDEX = 0;

    @Scheduled(cron = "0 0 0,12 * * *")
    public void scheduledRefresh() {
        log.info("Starting scheduled crypto rates refresh");

        List<Pair> cryptoPairs = pairRepo.findAllByType(PairType.CRYPTO);

        List<FxRate> updatedRates = cryptoPairs.stream()
                .map(this::fetchAndMapRate)
                .flatMap(Optional::stream)
                .toList();

        if (!updatedRates.isEmpty()) {
            repo.saveAll(updatedRates);
        }

        log.info("Completed crypto rates refresh. Updated {} symbols", updatedRates.size());
    }

    private Optional<FxRate> fetchAndMapRate(Pair pair) {
        return fetchRawKlines(pair.getSymbol())
                .flatMap(klines -> mapToFxRate(pair.getId(), klines));
    }

    private Optional<List<List<Object>>> fetchRawKlines(String symbol) {
        try {
            String url = String.format("%s?symbol=%s&interval=%s&limit=1",
                    baseUrl, symbol, KLINE_INTERVAL);

            String responseStr = restTemplate.getForObject(url, String.class);
            return Optional.ofNullable(objectMapper.readValue(responseStr, new TypeReference<>() {}));
        } catch (Exception e) {
            log.error("Failed to fetch crypto rate for symbol: {}", symbol, e);
            return Optional.empty();
        }
    }

    private Optional<FxRate> mapToFxRate(Long pairId, List<List<Object>> klines) {
        if (klines.isEmpty()) {
            return Optional.empty();
        }

        try {
            List<Object> kline = klines.getFirst();
            long openTimeMs = ((Number) kline.get(OPEN_TIME_INDEX)).longValue();
            BigDecimal closePrice = new BigDecimal(kline.get(CLOSE_PRICE_INDEX).toString());

            FxRate rate = new FxRate();
            rate.setPairId(pairId);
            rate.setFxCandleType(FxCandleType.D);
            rate.setPrice(closePrice);
            rate.setCandleDate(Instant.ofEpochMilli(openTimeMs)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate());

            return Optional.of(rate);
        } catch (Exception e) {
            log.error("Error parsing kline data for pairId: {}", pairId, e);
            return Optional.empty();
        }
    }
}
