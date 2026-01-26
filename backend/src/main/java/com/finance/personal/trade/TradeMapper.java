package com.finance.personal.trade;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TradeMapper {
    TradeDto toDto(Trade trade);
    Trade toEntity(TradeDto tradeDto);

    List<TradeDto> toDto(List<Trade> trade);
    List<Trade> toEntity(List<TradeDto> tradeDto);
}