package com.example.petcurrencyexchange.repositories;

import com.example.petcurrencyexchange.models.Currency;
import com.example.petcurrencyexchange.models.ExchangeRates;
import com.example.petcurrencyexchange.service.ConnectionUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesRepository {
    private final CurrencyRepository currencyRepository = new CurrencyRepository();
    private ExchangeRates createExchangeRatesById(ResultSet resultSet) throws SQLException {
        try {
            resultSet.next();
            Currency baseCurrency = null;
            Currency targetCurrency = null;
            if (currencyRepository.getCurrencyById(resultSet.getInt("basecurrencyid")).isPresent()) {
                baseCurrency = currencyRepository.
                        getCurrencyById(resultSet.getInt("basecurrencyid")).get();
            }
            if (currencyRepository.getCurrencyById(resultSet.getInt("targetcurrencyid")).isPresent()) {
                targetCurrency = currencyRepository.
                        getCurrencyById(resultSet.getInt("targetcurrencyid")).get();
            }
            return new ExchangeRates(resultSet.getInt("id"), baseCurrency, targetCurrency,
                    resultSet.getBigDecimal("rate"));
        } catch (SQLException e) {
            return null;
        }
    }
    public Optional<ExchangeRates> getExchangeRatesByCodes(String base, String target) throws SQLException {
        final String QUERY = "SELECT exchangerates.id, basecurrencyid, targetcurrencyid, rate " +
                "FROM exchangerates JOIN currencies c ON c.id = exchangerates.basecurrencyid " +
                "JOIN currencies c2 ON c2.id = exchangerates.targetcurrencyid WHERE c.code = ? AND c2.code = ?";
        try (Connection connection = ConnectionUtil.open();
             PreparedStatement statement = connection.prepareStatement(QUERY)) {
            statement.setString(1, base);
            statement.setString(2, target);
            return Optional.ofNullable(createExchangeRatesById(statement.executeQuery()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateExchangeRatesByCodes(int base, int target, BigDecimal rate) {
        final String QUERY = "UPDATE exchangerates SET rate = ? WHERE basecurrencyid = ? AND targetcurrencyid = ?";
        try (Connection connection = ConnectionUtil.open();
        PreparedStatement statement = connection.prepareStatement(QUERY)) {
            statement.setBigDecimal(1, rate);
            statement.setInt(2, base);
            statement.setInt(3, target);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addExchangeRates(String base, String target, BigDecimal rate) {
        final String QUERY = "INSERT INTO exchangerates (basecurrencyid, targetcurrencyid, rate) VALUES (?,?,?)";
        try (Connection connection = ConnectionUtil.open();
        PreparedStatement statement = connection.prepareStatement(QUERY)) {
            if (currencyRepository.getCurrencyByCode(base).isPresent() &&
            currencyRepository.getCurrencyByCode(target).isPresent()) {
                int baseId = currencyRepository.getCurrencyByCode(base).get().getId();
                int targetId = currencyRepository.getCurrencyByCode(target).get().getId();
                statement.setInt(1, baseId);
                statement.setInt(2, targetId);
                statement.setBigDecimal(3, rate);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<ExchangeRates> getExchangeRates() {
        List<ExchangeRates> exchangeRates = new ArrayList<>();
        final String QUERY = "SELECT id, basecurrencyid, targetcurrencyid, rate FROM exchangerates";
        try (Connection connection = ConnectionUtil.open();
             PreparedStatement statement = connection.prepareStatement(QUERY)) {
            ResultSet resultSet = statement.executeQuery();
            while (!resultSet.isLast()) {
                exchangeRates.add(createExchangeRatesById(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
