package com.finance.business.web.revenue;

import com.finance.business.persistence.revenue.BusinessRevenue;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BusinessRevenueMapper {
    BusinessRevenueDto toDto(BusinessRevenue profit);
    BusinessRevenue toEntity(BusinessRevenueDto dto);
    List<BusinessRevenueDto> toDto(List<BusinessRevenue> list);
}