package com.finance.personal.web.mapper;

import com.finance.personal.persistence.PersonalRevenueType;

import java.math.BigDecimal;

public record PersonalRevenueDto(
    Long id,
    PersonalRevenueType revenueType,
    BigDecimal amount
) {}