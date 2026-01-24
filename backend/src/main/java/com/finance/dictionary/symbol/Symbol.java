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

    @Column(name = "symbol", length = Integer.MAX_VALUE)
    private String symbol;

    @Column(name = "\"group\"", length = Integer.MAX_VALUE)
    private String group;
}