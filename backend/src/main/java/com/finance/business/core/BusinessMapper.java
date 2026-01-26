package com.finance.business.core;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BusinessMapper {
    BusinessDto toDto(Business business);
    Business toEntity(BusinessDto dto);
    List<BusinessDto> toDto(List<Business> list);
}