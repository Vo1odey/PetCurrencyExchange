package com.example.petcurrencyexchange.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Calculation {
    public static BigDecimal convertedAmount (BigDecimal amount, BigDecimal rate){
        BigDecimal one = new BigDecimal(1);
        BigDecimal rate100 = rate.multiply(new BigDecimal(100));
        BigDecimal priceToOne = one.divide(rate100, 10, RoundingMode.HALF_DOWN);
        return priceToOne.multiply(amount).setScale(2, RoundingMode.HALF_DOWN);
    }
    public static BigDecimal reverseConvertedAmount (BigDecimal amount, BigDecimal rate){
        BigDecimal priceToOneValue = rate.multiply(new BigDecimal(100));
        return amount.multiply(priceToOneValue).setScale(2, RoundingMode.HALF_DOWN);
    }
    public static BigDecimal crossConvertedAmount (BigDecimal fromRate, BigDecimal toRate, BigDecimal amount){
        return fromRate.divide(toRate, 2, RoundingMode.HALF_DOWN).multiply(amount).setScale(2, RoundingMode.HALF_DOWN);
    }
    public static BigDecimal crossRate (BigDecimal fromRate, BigDecimal toRate){
        return fromRate.divide(toRate, 2, RoundingMode.HALF_DOWN);
    }
}
