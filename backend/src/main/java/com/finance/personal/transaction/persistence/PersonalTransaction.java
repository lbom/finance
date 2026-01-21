package com.finance.personal.transaction.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "personal_transaction")
public class PersonalTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = Integer.MAX_VALUE)
    private PersonalTransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "spending_type", length = Integer.MAX_VALUE)
    private PersonalSpendingType spendingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "profit_type", length = Integer.MAX_VALUE)
    private PersonalProfitType profitType;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;
}
