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

@WebServlet(name = "CurrencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyRepository currencyRepository;
    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyRepository = (CurrencyRepository) config.getServletContext().getAttribute("currencyRepository");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Filter.setContentTypeAndCharacterEncoding(req, resp);
        if (req.getPathInfo().length() == 1) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Код валюты отсутсвует в адресе");
            return;
        }
        if (req.getPathInfo().length() < 4) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Валюта не найдена");
        } else {
            String code = req.getPathInfo().substring(1,4).toUpperCase();
            try {
                if (currencyRepository.getCurrencyByCode(code).isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Валюта не найдена");
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.writeValue(resp.getWriter(), currencyRepository.getCurrencyByCode(code).get());
                }
            } catch (SQLException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Валюта не найдена");
            }
        }
    }
}
