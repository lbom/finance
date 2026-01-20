package com.finance.business.web.transactions;

import com.finance.business.persistence.transactions.BusinessTransaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BusinessTransactionsMapper {
    BusinessTransactionDto toDto(BusinessTransaction expenses);
    BusinessTransaction toEntity(BusinessTransactionDto dto);
    List<BusinessTransactionDto> toDto(List<BusinessTransaction> list);
}