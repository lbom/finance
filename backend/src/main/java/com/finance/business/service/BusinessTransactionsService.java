package com.finance.business.service;

import com.finance.app.UserInterface;
import com.finance.business.persistence.transactions.BusinessTransaction;
import com.finance.business.persistence.transactions.BusinessTransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessTransactionsService {

    private final UserInterface userInterface;
    private final BusinessTransactionRepo repository;

    public List<BusinessTransaction> getAll() { return repository.findAll(); }
    public void save(BusinessTransaction expenses) { repository.save(expenses); }
}