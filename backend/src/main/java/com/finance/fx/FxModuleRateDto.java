package com.finance.fx;

import com.finance.fx.rate.FxCandleType;
import jakarta.annotation.Nonnull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FxModuleRateDto(
    @Nonnull Long pairId,
    @Nonnull Long baseCurrencyId,
    @Nonnull Long quoteCurrencyId,
    @Nonnull String symbol,
    @Nonnull FxCandleType fxCandleType,
    @Nonnull LocalDate candleDate,
    @Nonnull BigDecimal price
){}