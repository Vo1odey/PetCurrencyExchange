package com.example.petcurrencyexchange.repositories;

import com.example.petcurrencyexchange.models.Currency;
import com.example.petcurrencyexchange.utils.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class CurrencyRepository {
    private Currency createCurrency (ResultSet resultSet) throws SQLException {
        try {
            resultSet.next();
            return new Currency(resultSet.getInt("id"),
                    resultSet.getString("code"),
                    resultSet.getString("fullname"),
                    resultSet.getString("sign"));
        } catch (SQLException e) {
            return null;
        }
    }
    public Optional<Currency> getCurrencyByCode(String code) throws SQLException {
        final String QUERY = "SELECT id, code, fullname, sign FROM currencies WHERE code = ?";
        try (Connection connection = ConnectionUtil.open();
             PreparedStatement statement = connection.prepareStatement(QUERY)) {
            statement.setString(1, code);
            return Optional.ofNullable(createCurrency(statement.executeQuery()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
