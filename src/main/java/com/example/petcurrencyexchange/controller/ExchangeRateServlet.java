package com.example.petcurrencyexchange.controller;

import com.example.petcurrencyexchange.models.ExchangeRates;
import com.example.petcurrencyexchange.repositories.CurrencyRepository;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

@WebServlet(name = "ExchangeServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRatesRepository exchangeRatesRepository;
    private CurrencyRepository currencyRepository;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (method.equals("PATCH")) {
                doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
    @Override
    public void init(ServletConfig config) {
        exchangeRatesRepository = (ExchangeRatesRepository) config.getServletContext().getAttribute("exchangeRatesRepository");
        currencyRepository = (CurrencyRepository) config.getServletContext().getAttribute("currencyRepository");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Filter.setContentTypeAndCharacterEncoding(req, resp);
        if (req.getPathInfo().length() < 7) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Коды валют пары отсутствуют в адресе");
            return;
        }
        String base = req.getPathInfo().substring(1,4).toUpperCase();
        String target = req.getPathInfo().substring(4,7).toUpperCase();
        try {
            if (exchangeRatesRepository.getExchangeRatesByCodes(base, target).isPresent()) {
                writeAndPrintExchangeRates(resp, exchangeRatesRepository.getExchangeRatesByCodes(base, target).get());
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Обменный курс для пары не найден");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("basecurrencycode").length() < 3 ||
                req.getParameter("targetcurrencycode").length() < 3) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }
        String base = req.getParameter("basecurrencycode").toUpperCase();
        String target = req.getParameter("targetcurrencycode").toUpperCase();
        //Validate rate?
        BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(req.getParameter("rate"))).setScale(2, RoundingMode.HALF_DOWN);
        try {
            if (exchangeRatesRepository.getExchangeRatesByCodes(base, target).isEmpty()) {
                if (currencyRepository.getCurrencyByCode(base).isPresent() &&
                currencyRepository.getCurrencyByCode(target).isPresent()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    Filter.setContentTypeAndCharacterEncoding(req, resp);
                    exchangeRatesRepository.addExchangeRates(base, target, rate);
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.writeValue(resp.getWriter(), exchangeRatesRepository.getExchangeRatesByCodes(base, target).get());
                } else resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Введенная валюта не найдена");
            } else resp.sendError(HttpServletResponse.SC_CONFLICT, "Валютная пара с таким кодом уже существует");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Filter.setContentTypeAndCharacterEncoding(req, res);
        if (req.getPathInfo().length() < 7) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }
        String baseCode = req.getPathInfo().substring(1,4).toUpperCase();
        String targetCode = req.getPathInfo().substring(4,7).toUpperCase();
        BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(req.getParameter("rate"))).setScale
                (2, RoundingMode.HALF_DOWN);
        try {
            if (exchangeRatesRepository.getExchangeRatesByCodes(baseCode, targetCode).isPresent()) {
                ExchangeRates exchangeRates = exchangeRatesRepository.getExchangeRatesByCodes(baseCode, targetCode).get();
                int baseId = exchangeRates.getBaseCurrency().getId();
                int targetId = exchangeRates.getTargetCurrency().getId();
                exchangeRatesRepository.updateExchangeRatesByCodes(baseId, targetId, rate);
                writeAndPrintExchangeRates(res, exchangeRatesRepository.getExchangeRatesByCodes(baseCode, targetCode).get());
            } else res.sendError(HttpServletResponse.SC_NOT_FOUND, "Валютная пара отсутствует в базе данных ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void writeAndPrintExchangeRates(HttpServletResponse res, ExchangeRates printModel) throws IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(res.getWriter(), printModel);
    }
}
