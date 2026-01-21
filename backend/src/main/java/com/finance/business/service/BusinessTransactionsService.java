package com.finance.business.service;

import com.finance.app.UserInterface;
import com.finance.business.persistence.transactions.BusinessTransaction;
import com.finance.business.persistence.transactions.BusinessTransactionRepo;
import com.finance.business.persistence.transactions.BusinessTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessTransactionsService {

    private final UserInterface userInterface;
    private final BusinessTransactionRepo repository;

    public List<BusinessTransaction> getTransactionsByType(BusinessTransactionType type) {
        var userId = userInterface.retrieveUserId();
        return repository.getBusinessTransactionsByUserIdAndType(userId, type);
    }

    public void save(BusinessTransaction transaction) {
        var userId = userInterface.retrieveUserId();
        transaction.setUserId(userId);
        repository.save(transaction);
    }
}