package com.swnur.dao;

import com.swnur.DBConnectionManager;
import com.swnur.entity.Currency;
import com.swnur.exception.DBOperationException;
import com.swnur.exception.EntityExistsException;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

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
        final String query = "SELECT * FROM currency where code=?;";

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getCurrency(resultSet));
            }
        } catch (SQLException e) {
            throw new DBOperationException(
                    String.format("Failed to find currency with code '%s' from the database.", code)
            );
        }

        return Optional.empty();
    }

    @Override
    public Optional<Currency> findByID(Long id) {
        final String query = "SELECT * FROM currency WHERE id=?;";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getCurrency(resultSet));
            }

        } catch (SQLException e) {
            throw new DBOperationException(
                    String.format("Failed to find currency with id + '%d' from the database.", id)
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Currency> findAll() {
        final String query = "SELECT * FROM currency;";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Currency> currencies = new ArrayList<>();

            while (resultSet.next()) {
                currencies.add(getCurrency(resultSet));
            }

            return currencies;
        } catch (SQLException e) {
            throw new DBOperationException("Failed to find currencies from the database.");
        }
    }

    @Override
    public Currency insert(Currency entity) {
        final String query = "INSERT INTO currency (code, full_name, sign) VALUES (?, ?, ?) RETURNING *";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getFullName());
            preparedStatement.setString(3, entity.getSign());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new DBOperationException(
                        String.format("Failed to insert currency with code '%s' to the database.", entity.getCode())
                );
            }

            return getCurrency(resultSet);
        } catch (SQLException e) {
            if (e instanceof SQLiteException) {
                SQLiteException sqLiteException = (SQLiteException) e;
                if (sqLiteException.getResultCode().code == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                    throw new EntityExistsException(
                            String.format("Currency with code '%s' already exists.", entity.getCode())
                    );
                }
            }
            throw new DBOperationException(
                    String.format("Failed to insert currency with code '%s' to the database.", entity.getCode())
            );
        }
    }

    @Override
    public Optional<Currency> update(Currency entity) {
        final String query = "UPDATE currency SET (code, full_name, sign) = (?, ?, ?) WHERE id = ? RETURNING *";

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
            throw new DBOperationException(
                    String.format("Failed to update currency with id '%d' from the database.", entity.getId())
            );
        }

        return Optional.empty();
    }

    @Override
    public void delete(Long id) {
        final String query = "DELETE FROM currency WHERE id = ?";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DBOperationException(
                    String.format("Failed to delete currency with id '%d' from the database.", id)
            );
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
