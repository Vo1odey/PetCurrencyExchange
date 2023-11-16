package com.example.petcurrencyexchange.models;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Exchange {
    private int id;
    private Currency base;
    private Currency target;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
