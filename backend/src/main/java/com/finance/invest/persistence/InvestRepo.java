package com.finance.invest.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestRepo extends JpaRepository<Invest, Long> {
    List<Invest> getInvestsByPersonId(Long userId);
}
