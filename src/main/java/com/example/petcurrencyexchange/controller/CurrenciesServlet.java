package com.example.petcurrencyexchange.controller;

import com.example.petcurrencyexchange.repositories.CurrencyRepository;
import com.example.petcurrencyexchange.service.Filter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "CurrenciesServlet", value = "/currencies/*")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyRepository currencyRepository;
    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyRepository = (CurrencyRepository) config.getServletContext().getAttribute("currencyRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Filter.setContentTypeAndCharacterEncoding(req, resp);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(resp.getWriter(), currencyRepository.getCurrencies());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Filter.setContentTypeAndCharacterEncoding(req, resp);
        if (req.getParameter("name").isEmpty() || req.getParameter("code").length() < 3 ||
                req.getParameter("sign").isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }
        String name = req.getParameter("name");
        String code = req.getParameter("code").toUpperCase();
        String sign = req.getParameter("sign");
        try {
            if (currencyRepository.getCurrencyByCode(code).isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                currencyRepository.addCurrency(name, code, sign);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(resp.getWriter(), currencyRepository.getCurrencyByCode(code).get());
            } else resp.sendError(HttpServletResponse.SC_CONFLICT, "Такая валюта уже существует");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
