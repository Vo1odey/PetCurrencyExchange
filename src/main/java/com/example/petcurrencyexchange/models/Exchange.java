package com.example.petcurrencyexchange.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exchange {
    private int id;
    private Currency base;
    private Currency target;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
