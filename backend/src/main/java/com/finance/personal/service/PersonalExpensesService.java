package com.finance.personal.service;

import com.finance.personal.persistence.PersonalExpenses;
import com.finance.personal.persistence.PersonalExpensesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalExpensesService {

    private final PersonalExpensesRepo repository;

    public List<PersonalExpenses> getAll() { return repository.findAll(); }
    public void save(PersonalExpenses expenses) { repository.save(expenses); }
}
