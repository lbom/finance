package com.finance.person.balance.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "person_balance")
public class PersonBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "currency_id")
    private Long currencyId;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;
}
