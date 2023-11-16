package com.example.petcurrencyexchange.controller;

import com.example.petcurrencyexchange.repositories.CurrencyRepository;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
    public ContextListener() {}
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        CurrencyRepository currencyRepository = new CurrencyRepository();
        context.setAttribute("currencyRepository", currencyRepository);
    }

}
