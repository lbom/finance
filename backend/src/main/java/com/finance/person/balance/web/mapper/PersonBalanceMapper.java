package com.finance.person.balance.web.mapper;

import com.finance.person.balance.persistence.PersonBalance;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonBalanceMapper {
    PersonBalanceDto toDto(PersonBalance entity);
    PersonBalance toEntity(PersonBalanceDto dto);
    List<PersonBalanceDto> toDto(List<PersonBalance> list);
}
