package com.example.petcurrencyexchange.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;

public class Filter {
    public static void setContentTypeAndCharacterEncoding (HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        resp.addHeader("Content-Type", "application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
    }
    public static boolean validateCode(String str) {
        if (str.isEmpty()) {
            return false;
        }
        if (str.length() == 1) {
            return false;
        }
        if (str.length() < 4) {
            return false;
        }
        return true;
    }
}
