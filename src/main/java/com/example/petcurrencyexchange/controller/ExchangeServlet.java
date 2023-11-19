package com.example.petcurrencyexchange.controller;

import com.example.petcurrencyexchange.models.DtoExchange;
import com.example.petcurrencyexchange.models.ExchangeRates;
import com.example.petcurrencyexchange.repositories.ExchangeRatesRepository;
import com.example.petcurrencyexchange.utils.Calculation;
import com.example.petcurrencyexchange.utils.Filter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
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
    public void init(ServletConfig config) throws ServletException {
        exchangeRatesRepository = (ExchangeRatesRepository) config.getServletContext().getAttribute("exchangeRatesRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Filter.setContentTypeAndCharacterEncoding(req, resp);
        String from = req.getParameter("from").toUpperCase();
        String to = req.getParameter("to").toUpperCase();
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(
                req.getParameter("amount"))).setScale(2, RoundingMode.HALF_DOWN);
        String rubCode = "RUB";
        try {
            if (exchangeRatesRepository.getExchangeRatesByCodes(from, to).isPresent()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                ExchangeRates exchangeRatesModel = exchangeRatesRepository.getExchangeRatesByCodes(from, to).get();
                BigDecimal convertedAmount = Calculation.convertedAmount(amount, exchangeRatesModel.getRate());
                DtoExchange DtoModel = new DtoExchange(exchangeRatesModel.getBaseCurrency(),
                        exchangeRatesModel.getTargetCurrency(), exchangeRatesModel.getRate(), amount, convertedAmount);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(resp.getWriter(), DtoModel);
            }
            if (exchangeRatesRepository.getExchangeRatesByCodes(from, to).isEmpty()) {
                if (exchangeRatesRepository.getExchangeRatesByCodes(to, from).isPresent()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    ExchangeRates exchangeRatesModel = exchangeRatesRepository.getExchangeRatesByCodes(to, from).get();
                    BigDecimal convertedAmount = Calculation.reverseConvertedAmount(amount, exchangeRatesModel.getRate());
                    DtoExchange dtoModel = new DtoExchange(exchangeRatesModel.getBaseCurrency(),
                            exchangeRatesModel.getTargetCurrency(), exchangeRatesModel.getRate(), amount, convertedAmount);
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.writeValue(resp.getWriter(), dtoModel);
                }
            }
            if (exchangeRatesRepository.getExchangeRatesByCodes(rubCode, from).isPresent() &&
                    exchangeRatesRepository.getExchangeRatesByCodes(rubCode, to).isPresent()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                ExchangeRates fromRateModel = exchangeRatesRepository.getExchangeRatesByCodes(rubCode, from).get();
                ExchangeRates toRateModel = exchangeRatesRepository.getExchangeRatesByCodes(rubCode, to).get();
                BigDecimal rate = Calculation.crossRate(fromRateModel.getRate(), toRateModel.getRate());
                BigDecimal convertedAmount = Calculation.crossConvertedAmount(fromRateModel.getRate(), toRateModel.getRate(), amount);
                DtoExchange dtoModel = new DtoExchange(fromRateModel.getTargetCurrency(), toRateModel.getTargetCurrency(),
                        rate, amount, convertedAmount);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(resp.getWriter(), dtoModel);
            } else resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Валюта не найдена");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
