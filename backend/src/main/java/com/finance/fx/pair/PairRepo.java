package com.finance.fx.pair;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PairRepo extends JpaRepository<Pair, Long> {
    List<Pair> findAllByType(PairType type);
    List<Pair> findAllByTypeAndBaseCurrencyId(PairType type, Long baseCurrencyId);
}
