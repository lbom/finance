package com.finance.invest.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestRepo extends JpaRepository<Invest, Long> {
}
