package com.finance.dictionary.symbol;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "symbol")
public class Symbol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "institution_id", nullable = false)
    private Long institutionId;

    @Column(name = "symbol")
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"group\"")
    private SymbolGroup group;
}