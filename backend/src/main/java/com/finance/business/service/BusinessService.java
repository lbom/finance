package com.finance.business.service;

import com.finance.app.UserInterface;
import com.finance.business.persistence.core.Business;
import com.finance.business.persistence.core.BusinessRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final UserInterface userInterface;
    private final BusinessRepo repository;

    public List<Business> getAll() {
        var userId = userInterface.retrieveUserId();
        return repository.findBusinessByUserId(userId);
    }
    public void save(Business business) {
        var userId = userInterface.retrieveUserId();
        business.setUserId(userId);
        repository.save(business);
    }
}