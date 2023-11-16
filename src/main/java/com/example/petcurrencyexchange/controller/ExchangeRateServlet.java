package com.example.petcurrencyexchange.controller;

import com.example.petcurrencyexchange.repositories.ExchangeRatesRepository;
import com.example.petcurrencyexchange.utils.Filter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "ExchangeServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRatesRepository exchangeRatesRepository;
    @Override
    public void init(ServletConfig config) throws ServletException {
        exchangeRatesRepository = (ExchangeRatesRepository) config.getServletContext().getAttribute("exchangeRatesRepository");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Filter.setContentTypeAndCharacterEncoding(req, resp);
        if (req.getPathInfo().length() < 7) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Коды валют пары отсутствуют в адресе");
            return;
        }
        String base = req.getPathInfo().substring(1,4).toUpperCase();
        String target = req.getPathInfo().substring(4,7).toUpperCase();
        try {
            if (exchangeRatesRepository.getExchangeRatesByCodes(base, target).isPresent()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(resp.getWriter(), exchangeRatesRepository.getExchangeRatesByCodes(base, target).get());
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Обменный курс для пары не найден");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
