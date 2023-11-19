package com.example.petcurrencyexchange.controller;

import com.example.petcurrencyexchange.models.Currency;
import com.example.petcurrencyexchange.models.DtoExchange;
import com.example.petcurrencyexchange.models.ExchangeRates;
import com.example.petcurrencyexchange.repositories.ExchangeRatesRepository;
import com.example.petcurrencyexchange.utils.Calculation;
import com.example.petcurrencyexchange.utils.Filter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

@WebServlet(value = "/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRatesRepository exchangeRatesRepository;
    @Override
    public void init(ServletConfig config) {
        exchangeRatesRepository = (ExchangeRatesRepository) config.getServletContext()
                .getAttribute("exchangeRatesRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Filter.setContentTypeAndCharacterEncoding(req, resp);
        String from = req.getParameter("from").toUpperCase();
        String to = req.getParameter("to").toUpperCase();
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(req.getParameter("amount"))).setScale
                (2, RoundingMode.HALF_DOWN);
        try {
            if (exchangeRatesRepository.getExchangeRatesByCodes(from, to).isPresent()) {
                ExchangeRates exchangeRatesModel = exchangeRatesRepository.getExchangeRatesByCodes(from, to).get();
                BigDecimal convertedAmount = Calculation.convertedAmount(amount, exchangeRatesModel.getRate());
                createdAndPrintDTO(exchangeRatesModel.getBaseCurrency(), exchangeRatesModel.getTargetCurrency(),
                        exchangeRatesModel.getRate(), amount, convertedAmount, resp);
            }
            if (exchangeRatesRepository.getExchangeRatesByCodes(from, to).isEmpty()) {
                if (exchangeRatesRepository.getExchangeRatesByCodes(to, from).isPresent()) {
                    ExchangeRates exchangeRatesModel = exchangeRatesRepository.getExchangeRatesByCodes(to, from).get();
                    BigDecimal convertedAmount = Calculation.reverseConvertedAmount(amount, exchangeRatesModel.getRate());
                    createdAndPrintDTO(exchangeRatesModel.getBaseCurrency(), exchangeRatesModel.getTargetCurrency(),
                            exchangeRatesModel.getRate(), amount, convertedAmount, resp);
                }
            }
            String rubCode = "RUB";
            if (exchangeRatesRepository.getExchangeRatesByCodes(rubCode, from).isPresent() &&
                    exchangeRatesRepository.getExchangeRatesByCodes(rubCode, to).isPresent()) {
                ExchangeRates fromRateModel = exchangeRatesRepository.getExchangeRatesByCodes(rubCode, from).get();
                ExchangeRates toRateModel = exchangeRatesRepository.getExchangeRatesByCodes(rubCode, to).get();
                BigDecimal rate = Calculation.crossRate(fromRateModel.getRate(), toRateModel.getRate());
                BigDecimal convertedAmount = Calculation.crossConvertedAmount(fromRateModel.getRate(), toRateModel.getRate(), amount);
                createdAndPrintDTO(fromRateModel.getTargetCurrency(), toRateModel.getTargetCurrency(), rate, amount,
                        convertedAmount, resp);
            } else resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Валюта не найдена");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    void createdAndPrintDTO (Currency from, Currency to, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount,
                             HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        DtoExchange dtoExchange = new DtoExchange(from, to, rate, amount, convertedAmount);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(resp.getWriter(), dtoExchange);
    }
}
