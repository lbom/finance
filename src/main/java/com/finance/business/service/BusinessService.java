package com.finance.business.service;

import com.finance.business.persistence.core.Business;
import com.finance.business.persistence.core.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository repository;

    public List<Business> getAll() { return repository.findAll(); }
    public void save(Business business) { repository.save(business); }
}