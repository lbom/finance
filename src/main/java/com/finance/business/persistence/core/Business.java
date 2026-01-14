package com.finance.business.persistence.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "business")
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "idea", length = Integer.MAX_VALUE)
    private String idea;

    @Column(name = "overall_amount", precision = 10, scale = 2)
    private BigDecimal overallAmount;

    @Column(name = "overall_profit", precision = 10, scale = 2)
    private BigDecimal overallProfit;


}