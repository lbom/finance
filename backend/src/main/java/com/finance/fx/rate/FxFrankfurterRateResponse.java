package com.finance.fx.rate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record FxFrankfurterRateResponse(
    BigDecimal amount,
    String base,
    LocalDate date,
    Map<String, BigDecimal> rates
) {}
