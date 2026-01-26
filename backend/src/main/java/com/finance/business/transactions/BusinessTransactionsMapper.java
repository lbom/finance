package com.finance.business.transactions;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BusinessTransactionsMapper {
    BusinessTransactionDto toDto(BusinessTransaction expenses);
    BusinessTransaction toEntity(BusinessTransactionDto dto);
    List<BusinessTransactionDto> toDto(List<BusinessTransaction> list);
}