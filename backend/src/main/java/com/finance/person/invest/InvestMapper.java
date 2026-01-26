package com.finance.person.invest;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvestMapper {
    InvestDto toDto(Invest invest);
    Invest toEntity(InvestDto investDto);

    List<InvestDto> toDto(List<Invest> invest);
    List<Invest> toEntity(List<InvestDto> investDto);
}