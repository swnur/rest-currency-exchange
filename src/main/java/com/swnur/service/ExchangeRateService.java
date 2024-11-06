package com.swnur.service;

import com.swnur.dao.CurrencyDAO;
import com.swnur.dao.CurrencyDAOImpl;
import com.swnur.dao.ExchangeRateDAO;
import com.swnur.dao.ExchangeRateDAOImpl;
import com.swnur.dto.ExchangeRateRequestDTO;
import com.swnur.entity.Currency;
import com.swnur.entity.ExchangeRate;
import com.swnur.exception.NotFoundException;

public class ExchangeRateService {

    private final CurrencyDAO currencyDAO = new CurrencyDAOImpl();
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl();

    public ExchangeRate insert(ExchangeRateRequestDTO exchangeRateRequestDTO) {
        String baseCurrencyCode = exchangeRateRequestDTO.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateRequestDTO.getTargetCurrencyCode();

        Currency baseCurrency = currencyDAO.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Currency with code '%s' not found", baseCurrencyCode)));
        Currency targetCurrency = currencyDAO.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Currency with code '%s' not found", targetCurrencyCode)));

        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, exchangeRateRequestDTO.getRate());

        return exchangeRateDAO.insert(exchangeRate);
    }

    public ExchangeRate update(ExchangeRateRequestDTO exchangeRateRequestDTO) {
        String baseCurrencyCode = exchangeRateRequestDTO.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateRequestDTO.getTargetCurrencyCode();

        Currency baseCurrency = currencyDAO.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Currency with code '%s' not found", baseCurrencyCode)));
        Currency targetCurrency = currencyDAO.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Currency with code '%s' not found", targetCurrencyCode)));

        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, exchangeRateRequestDTO.getRate());

        return exchangeRateDAO.update(exchangeRate)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Failed to update exchange rate with code from '%s' to '%s', no such exchange rate was found"
                                , baseCurrencyCode, targetCurrencyCode)
                ));
    }
}
