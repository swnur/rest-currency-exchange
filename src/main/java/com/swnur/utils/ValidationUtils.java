package com.swnur.utils;

import com.swnur.dto.CurrencyRequestDTO;
import com.swnur.exception.InvalidParameterException;

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