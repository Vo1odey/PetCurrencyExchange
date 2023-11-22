package com.example.petcurrencyexchange.repositories;

import com.example.petcurrencyexchange.models.Currency;
import com.example.petcurrencyexchange.service.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class CurrencyRepository {
    private Currency createCurrency(ResultSet resultSet) throws SQLException {
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

    public Optional<Currency> getCurrencyById(int id) throws SQLException {
        final String QUERY = "SELECT id, code, fullname, sign FROM currencies WHERE id = ?";
        try (Connection connection = ConnectionUtil.open();
             PreparedStatement statement = connection.prepareStatement(QUERY)) {
            statement.setInt(1, id);
            return Optional.ofNullable(createCurrency(statement.executeQuery()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCurrency(String name, String code, String sign) {
        final String QUERY = "INSERT INTO currencies (fullname, code, sign) VALUES (?,?,?)";
        try (Connection connection = ConnectionUtil.open();
             PreparedStatement statement = connection.prepareStatement(QUERY)) {
            statement.setString(1, name);
            statement.setString(2, code);
            statement.setString(3, sign);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
