package com.finance.dictionary.symbol;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SymbolMapper {
    SymbolDto toDto(Symbol entity);
    Symbol toEntity(SymbolDto dto);
    List<SymbolDto> toDto(List<Symbol> list);
}
