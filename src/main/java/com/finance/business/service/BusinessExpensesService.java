package com.finance.business.service;

import com.finance.business.persistence.expenses.BusinessExpenses;
import com.finance.business.persistence.expenses.BusinessExpensesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessExpensesService {

    private final BusinessExpensesRepo repository;

    public List<BusinessExpenses> getAll() { return repository.findAll(); }
    public void save(BusinessExpenses expenses) { repository.save(expenses); }
}