package com.finance.fx.pair;

import jakarta.annotation.Nonnull;

public record PairDto(
    Long id,
    @Nonnull Long baseCurrencyId,
    @Nonnull Long quoteCurrencyId
) {}
