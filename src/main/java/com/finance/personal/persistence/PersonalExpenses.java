package com.finance.personal.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "personal_expenses")
public class PersonalExpenses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "expense_type", length = Integer.MAX_VALUE)
    private PersonalExpensesType expenseType;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;
}
