package com.finance.fx.pair;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "fx_pair")
public class Pair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "base_currency_id", nullable = false)
    private Long baseCurrencyId;

    @Column(name = "quote_currency_id", nullable = false)
    private Long quoteCurrencyId;

    private String symbol;

    @Enumerated(EnumType.STRING)
    private PairType type;
}
