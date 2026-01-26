package com.finance.dictionary.currency;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyDto toDto(Currency entity);
    Currency toEntity(CurrencyDto dto);
    List<CurrencyDto> toDto(List<Currency> list);
}
