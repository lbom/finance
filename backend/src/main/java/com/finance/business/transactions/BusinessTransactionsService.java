package com.finance.business.transactions;

import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessTransactionsService {

    private final UserModule userModule;
    private final BusinessTransactionRepo repository;

    public List<BusinessTransaction> getTransactionsByType(BusinessTransactionType type) {
        var userId = userModule.retrieveUserId();
        return repository.getBusinessTransactionsByUserIdAndType(userId, type);
    }

    public void save(BusinessTransaction transaction) {
        var userId = userModule.retrieveUserId();
        transaction.setUserId(userId);
        repository.save(transaction);
    }
}