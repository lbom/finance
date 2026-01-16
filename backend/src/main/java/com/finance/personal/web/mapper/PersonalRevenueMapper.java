package com.finance.personal.web.mapper;

import com.finance.personal.persistence.PersonalRevenue;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonalRevenueMapper {
    PersonalRevenueDto toDto(PersonalRevenue entity);
    PersonalRevenue toEntity(PersonalRevenueDto dto);
    List<PersonalRevenueDto> toDto(List<PersonalRevenue> list);
}