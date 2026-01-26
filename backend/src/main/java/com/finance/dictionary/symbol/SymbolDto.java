package com.finance.dictionary.symbol;

import jakarta.annotation.Nonnull;

public record SymbolDto(
    Long id,
    Long institutionId,
    @Nonnull String symbol,
    @Nonnull SymbolGroup group
) {}
