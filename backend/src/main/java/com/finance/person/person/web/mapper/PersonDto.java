package com.finance.person.person.web.mapper;

import java.math.BigDecimal;

public record PersonDto(
    Long id,
    Long currencyId,
    BigDecimal amount
) {}
