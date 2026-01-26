package com.finance.dictionary.currency;

import jakarta.annotation.Nonnull;

public record CurrencyDto(
    Long id,
    @Nonnull String symbol,
    @Nonnull String name
) {}
