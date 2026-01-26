package com.finance.business.core;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepo extends JpaRepository<Business, Long> {
    List<Business> findBusinessByUserId(Long userId);
}
