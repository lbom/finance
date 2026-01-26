package com.finance.person.transaction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "person_transaction")
public class PersonTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "balance_id", nullable = false)
    private Long balanceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = Integer.MAX_VALUE)
    private PersonTransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "spending_type", length = Integer.MAX_VALUE)
    private PersonSpendingType spendingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "profit_type", length = Integer.MAX_VALUE)
    private PersonProfitType profitType;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;
}
