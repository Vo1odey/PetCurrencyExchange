package com.example.petcurrencyexchange.controller;

import com.example.petcurrencyexchange.repositories.ExchangeRatesRepository;
import com.example.petcurrencyexchange.service.Filter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebServlet(name = "ExchangeRates", value = "/exchangeRates/*")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRatesRepository exchangeRatesRepository;
    @Override
    public void init(ServletConfig config) throws ServletException {
        exchangeRatesRepository = (ExchangeRatesRepository) config.getServletContext().getAttribute("exchangeRatesRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Filter.setContentTypeAndCharacterEncoding(req, resp);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(resp.getWriter(), exchangeRatesRepository.getExchangeRates());
    }
}
