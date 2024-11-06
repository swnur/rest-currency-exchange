package com.swnur.dao;

import com.swnur.DBConnectionManager;
import com.swnur.entity.Currency;
import com.swnur.entity.ExchangeRate;
import com.swnur.exception.DBOperationException;
import com.swnur.exception.EntityExistsException;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAOImpl implements ExchangeRateDAO {

    @Override
    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) {
        final String query =
                "  SELECT" +
                        "    er.id AS id," +
                        "    bc.id AS base_id," +
                        "    bc.code AS base_code," +
                        "    bc.full_name AS base_name," +
                        "    bc.sign AS base_sign," +
                        "    tc.id AS target_id," +
                        "    tc.code AS target_code," +
                        "    tc.full_name AS target_name," +
                        "    tc.sign AS target_sign," +
                        "    er.rate AS rate" +
                        "  FROM exchange_rate er" +
                        "  JOIN currency bc ON er.base_currency_id = bc.id" +
                        "  JOIN currency tc ON er.target_currency_id = tc.id" +
                        "  WHERE (" +
                        "    base_currency_id = (SELECT c.id FROM currency c WHERE c.code = ?) AND" +
                        "    target_currency_id = (SELECT c2.id FROM currency c2 WHERE c2.code = ?)" +
                        "  )";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new DBOperationException(
                    String.format("Failed to find exchange rate with codes from '%s' to '%s' from the database"
                        , baseCurrencyCode, targetCurrencyCode)
            );
        }

        return Optional.empty();
    }

    @Override
    public Optional<ExchangeRate> findByID(Long id) {
        final String query =
                "  SELECT" +
                        "    er.id AS id," +
                        "    bc.id AS base_id," +
                        "    bc.code AS base_code," +
                        "    bc.full_name AS base_name," +
                        "    bc.sign AS base_sign," +
                        "    tc.id AS target_id," +
                        "    tc.code AS target_code," +
                        "    tc.full_name AS target_name," +
                        "    tc.sign AS target_sign," +
                        "    er.rate AS rate" +
                        "  FROM exchange_rate er" +
                        "  JOIN currency bc ON er.base_currency_id = bc.id" +
                        "  JOIN currency tc ON er.target_currency_id = tc.id" +
                        "  WHERE er.id = ?";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new DBOperationException(
                    String.format("Failed to find data with id '%d' from the database.", id)
            );
        }

        return Optional.empty();
    }

    @Override
    public List<ExchangeRate> findAll() {
        final String query =
                "SELECT" +
                        "    er.id AS id," +
                        "    bc.id AS base_id," +
                        "    bc.code AS base_code," +
                        "    bc.full_name AS base_name," +
                        "    bc.sign AS base_sign," +
                        "    tc.id AS target_id," +
                        "    tc.code AS target_code," +
                        "    tc.full_name AS target_name," +
                        "    tc.sign AS target_sign," +
                        "    er.rate AS rate" +
                        "  FROM exchange_rate er" +
                        "  JOIN currency bc ON er.base_currency_id = bc.id" +
                        "  JOIN currency tc ON er.target_currency_id = tc.id" +
                        "  ORDER BY er.id";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();

            while (resultSet.next()) {
                exchangeRates.add(getExchangeRate(resultSet));
            }

            return exchangeRates;
        } catch (SQLException e) {
            throw new DBOperationException("Failed to find exchange rates from the database.");
        }
    }

    @Override
    public ExchangeRate insert(ExchangeRate entity) {
        final String query = "INSERT INTO exchange_rate (base_currency_id, target_currency_id, rate)" +
                "VALUES (?, ?, ?) RETURNING id;";

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, entity.getBaseCurrency().getCode());
            preparedStatement.setString(2, entity.getTargetCurrency().getCode());
            preparedStatement.setBigDecimal(3, entity.getRate());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new DBOperationException(
                        String.format("Failed to insert '%s' and '%s' to the database.",
                                entity.getBaseCurrency().getCode(), entity.getTargetCurrency().getCode())
                );
            }

            entity.setId(resultSet.getLong("id"));
            return entity;
        } catch (SQLException e) {
            if (e instanceof SQLiteException) {
                SQLiteException exception = (SQLiteException) e;
                if (exception.getResultCode().code == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                    throw new EntityExistsException(
                            String.format("Exchange rate '%s' and '%s' already exists",
                                    entity.getBaseCurrency().getCode(), entity.getTargetCurrency().getCode())
                    );
                }
            }
            throw new DBOperationException(
                    String.format("Failed to insert rate '%s' to '%s' to the database.",
                            entity.getBaseCurrency().getCode(), entity.getTargetCurrency().getCode())
            );
        }
    }

    @Override
    public Optional<ExchangeRate> update(ExchangeRate entity) {
        final String query = "UPDATE exchange_rate " +
                "SET rate = ? " +
                "WHERE base_currency_id = ? AND target_currency_id = ? RETURNING id;";

        try (Connection connection = DBConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setBigDecimal(1, entity.getRate());
            preparedStatement.setLong(2, entity.getBaseCurrency().getId());
            preparedStatement.setLong(3, entity.getTargetCurrency().getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                entity.setId(resultSet.getLong("id"));
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            throw new DBOperationException(
                    String.format("Failed to update entity's rate with id '%d' from the database.", entity.getId())
            );
        }

        return Optional.empty();
    }

    @Override
    public void delete(Long id) {
        final String query = "DELETE FROM exchange_rate WHERE id=?;";

        try (Connection connection = DBConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DBOperationException(
                    String.format("Failed to delete exchange rate with id '%s' from the database.", id)
            );
        }
    }

    private static ExchangeRate getExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getLong("id"),
                new Currency(
                        resultSet.getLong("base_id"),
                        resultSet.getString("base_code"),
                        resultSet.getString("base_name"),
                        resultSet.getString("base_sign")
                ),
                new Currency(
                        resultSet.getLong("target_id"),
                        resultSet.getString("target_code"),
                        resultSet.getString("target_name"),
                        resultSet.getString("target_sign")
                ),
                resultSet.getBigDecimal("rate")
        );
    }
}
