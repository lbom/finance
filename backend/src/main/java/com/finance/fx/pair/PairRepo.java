package com.finance.fx.pair;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PairRepo extends JpaRepository<Pair, Long> {
    List<Pair> findAllByType(PairType type);
    List<Pair> findAllByTypeAndBaseCurrencyId(PairType type, Long baseCurrencyId);

    @Query("SELECT p FROM Pair p WHERE p.type = :type AND (p.baseCurrencyId = :currencyId OR p.quoteCurrencyId = :currencyId)")
    List<Pair> findAllByTypeAndCurrencyId(@Param("type") PairType type, @Param("currencyId") Long currencyId);
}
