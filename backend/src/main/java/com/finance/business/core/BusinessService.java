package com.finance.business.core;

import com.finance.app.UserModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final UserModule userModule;
    private final BusinessRepo repository;

    public List<Business> getAll() {
        var userId = userModule.retrieveUserId();
        return repository.findBusinessByUserId(userId);
    }
    public void save(Business business) {
        var userId = userModule.retrieveUserId();
        business.setUserId(userId);
        repository.save(business);
    }
}