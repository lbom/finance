package com.finance.business.service;

import com.finance.business.persistence.revenue.BusinessRevenue;
import com.finance.business.persistence.revenue.BusinessRevenueRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessRevenueService {

    private final BusinessRevenueRepo repository;

    public List<BusinessRevenue> getAll() { return repository.findAll(); }
    public void save(BusinessRevenue revenue) { repository.save(revenue); }
}