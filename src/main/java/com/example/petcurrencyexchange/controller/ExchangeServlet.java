package com.example.petcurrencyexchange.controller;

import com.example.petcurrencyexchange.repositories.ExchangeRatesRepository;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet(name = "ExchangeServlet", value = "/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRatesRepository exchangeRatesRepository;
    @Override
    public void init(ServletConfig config) throws ServletException {
        exchangeRatesRepository = (ExchangeRatesRepository) config.getServletContext().getAttribute("exchangeRatesRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(
                req.getParameter("amount"))).setScale(2, RoundingMode.HALF_DOWN);

    }
}
