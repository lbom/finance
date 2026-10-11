package com.finance.personal.balance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Column(name = "institution_id", nullable = false)
    private Long institutionId;

    @Column(name = "currency_id")
    private Long currencyId;

    @Column(name = "amount", precision = 28, scale = 8)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PersonBalanceType type;

    @Column(name = "interest_rate", precision = 10, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "last_profit_date")
    private LocalDate lastProfitDate;
}
