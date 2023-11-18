package com.example.petcurrencyexchange.utils;

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
}
