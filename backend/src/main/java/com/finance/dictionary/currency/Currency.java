package com.finance.dictionary.currency;

import jakarta.persistence.*;

@Entity
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "name", precision = 19, scale = 2)
    private String name;
}
