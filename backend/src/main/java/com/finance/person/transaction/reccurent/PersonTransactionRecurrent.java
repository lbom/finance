package com.finance.person.transaction.reccurent;

import com.finance.person.transaction.PersonProfitType;
import com.finance.person.transaction.PersonSpendingType;
import com.finance.person.transaction.PersonTransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "person_transaction_recurrent")
public class PersonTransactionRecurrent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "balance_id", nullable = false)
    private Long balanceId;

    @Column(name = "period_days")
    private Integer periodDays;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "last_run_date")
    private LocalDateTime lastRunDate;

    @Column(name = "name")
    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = Integer.MAX_VALUE)
    private PersonTransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "spending_type", length = Integer.MAX_VALUE)
    private PersonSpendingType spendingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "profit_type", length = Integer.MAX_VALUE)
    private PersonProfitType profitType;
}


