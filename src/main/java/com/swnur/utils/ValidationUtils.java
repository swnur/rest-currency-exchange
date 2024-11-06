package com.swnur.utils;

import com.swnur.dto.CurrencyRequestDTO;
import com.swnur.dto.ExchangeRateRequestDTO;
import com.swnur.exception.InvalidParameterException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtils {

    private static final Set<String> currencyCodes;

    static {
        Set<Currency> currencies = Currency.getAvailableCurrencies();
        currencyCodes = currencies.stream()
                .map(Currency::getCurrencyCode)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static void validate(CurrencyRequestDTO currencyRequestDto) {
        checkNotNullOrBlank(currencyRequestDto.getCode(), "code");
        checkNotNullOrBlank(currencyRequestDto.getName(), "name");
        checkNotNullOrBlank(currencyRequestDto.getSign(), "sign");

        validateCurrencyCode(currencyRequestDto.getCode());
    }

    public static void validate(ExchangeRateRequestDTO exchangeRateRequestDto) {
        String baseCurrencyCode = exchangeRateRequestDto.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateRequestDto.getTargetCurrencyCode();
        BigDecimal rate = exchangeRateRequestDto.getRate();

        if (baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter - baseCurrencyCode");
        }

        if (targetCurrencyCode == null || targetCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter - targetCurrencyCode");
        }

        if (rate == null) {
            throw new InvalidParameterException("Missing parameter - rate");
        }

        if (rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidParameterException("Invalid parameter - rate must be non-negative");
        }

        validateCurrencyCode(baseCurrencyCode);
        validateCurrencyCode(targetCurrencyCode);
    }

    private static void checkNotNullOrBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidParameterException("Missing parameter - " + fieldName);
        }
    }

    public static void validateCurrencyCode(String code) {
        if (code.length() != 3) {
            throw new InvalidParameterException("Currency code must contain exactly 3 letters");
        }

        if (!currencyCodes.contains(code)) {
            throw new InvalidParameterException("Currency code must be in ISO 4217 format");
        }
    }
}