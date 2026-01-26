package com.finance.person.invest;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "investment")
@NoArgsConstructor
@AllArgsConstructor
public class Invest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "amount", precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "reason", length = Integer.MAX_VALUE)
    private String reason;

    @Column(name = "profit", precision = 19, scale = 4)
    private BigDecimal profit;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "type", length = Integer.MAX_VALUE)
    private String type;

    private Integer symbolId;
    private Integer institutionId;
}