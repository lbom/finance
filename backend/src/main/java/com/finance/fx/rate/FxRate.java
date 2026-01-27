package com.finance.fx.rate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "fx_rate")
public class FxRate {

    @Id
    private Long pairId;

    private BigDecimal price;

    private LocalDate candleDate;

    @Enumerated(EnumType.STRING)
    private FxCandleType candleType;
}
