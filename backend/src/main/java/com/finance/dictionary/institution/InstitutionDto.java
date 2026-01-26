package com.finance.dictionary.institution;

import jakarta.annotation.Nonnull;

public record InstitutionDto(
    Long id,
    @Nonnull String name,
    @Nonnull String country
) {}