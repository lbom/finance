package com.finance.fx.rate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FxRateRepo extends JpaRepository<FxRate, Long> {}