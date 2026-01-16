package com.finance.personal.service;

import com.finance.personal.persistence.PersonalRevenue;
import com.finance.personal.persistence.PersonalRevenueRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalRevenueService {

    private final PersonalRevenueRepo repository;

    public List<PersonalRevenue> getAll() { return repository.findAll(); }
    public void save(PersonalRevenue revenue) { repository.save(revenue); }
}