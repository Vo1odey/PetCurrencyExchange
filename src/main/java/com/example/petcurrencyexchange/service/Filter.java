package com.example.petcurrencyexchange.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

public class Filter {
    public static void setContentTypeAndCharacterEncoding (HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        resp.addHeader("Content-Type", "application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
    }
    public static boolean isValidDecimal(String decimal) {
        try {
            BigDecimal result = BigDecimal.valueOf(Double.parseDouble(decimal));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
