package com.finance.fx.pair;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PairMapper {
    PairDto toDto(Pair entity);
    Pair toEntity(PairDto dto);
    List<PairDto> toDto(List<Pair> list);
}
