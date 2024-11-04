package com.swnur.dao;

import com.swnur.DBConnectionManager;
import com.swnur.entity.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAOImpl implements CurrencyDAO {

    @Override
    public Optional<Currency> findByCode(String code) {
        final String query = "SELECT * FROM Currencies where code=?;";

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getCurrency(resultSet));
            }
        } catch (SQLException e) {
            // TODO: handle exception
        }

        return Optional.empty();
    }

    @Override
    public Optional<Currency> findByID(Long aLong) {
        final String query = "SELECT * FROM Currencies WHERE id=?;";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getCurrency(resultSet));
            }

        } catch (SQLException e) {
            // TODO: handle exception
        }

        return Optional.empty();
    }

    @Override
    public List<Currency> findAll() {
        final String query = "SELECT * FROM Currencies;";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Currency> currencies = new ArrayList<>();

            while (resultSet.next()) {
                currencies.add(getCurrency(resultSet));
            }

            return currencies;
        } catch (SQLException e) {
            // TODO: handle exception
            return List.of();
        }
    }

    @Override
    public Currency insert(Currency entity) {
        final String query = "INSERT INTO Currencies (code, full_name, sign) VALUES (?, ?, ?) RETURNING *";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getFullName());
            preparedStatement.setString(3, entity.getSign());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new SQLException();
            }

            return getCurrency(resultSet);
        } catch (SQLException e) {
            // TODO: handle exception
        }

        return null;
    }

    @Override
    public Optional<Currency> update(Currency entity) {
        final String query = "UPDATE Currencies SET (code, full_name, sign) = (?, ?, ?) WHERE id = ? RETURNING *";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getFullName());
            preparedStatement.setString(3, entity.getSign());
            preparedStatement.setLong(4, entity.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getCurrency(resultSet));
            }
        } catch (SQLException e) {
            // TODO: handle exception
        }

        return Optional.empty();
    }

    @Override
    public void delete(Long aLong) {
        final String query = "DELETE FROM Currencies WHERE id = ?";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, aLong);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // TODO: handle exception
        }
    }

    private static Currency getCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }
}
