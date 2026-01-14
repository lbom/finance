package com.finance.business.persistence.revenue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "business_profits")
public class BusinessRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "product", length = Integer.MAX_VALUE)
    private String product;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;


}