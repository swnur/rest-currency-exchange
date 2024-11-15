package com.swnur.utils;

import com.swnur.dto.CurrencyRequestDTO;
import com.swnur.dto.CurrencyResponseDTO;
import com.swnur.dto.ExchangeRateResponseDTO;
import com.swnur.entity.Currency;
import com.swnur.entity.ExchangeRate;
import org.modelmapper.ModelMapper;

public class MappingUtils {
    private static final ModelMapper MODEL_MAPPER;

    static {
        MODEL_MAPPER = new ModelMapper();

        MODEL_MAPPER.typeMap(CurrencyRequestDTO.class, Currency.class)
                .addMapping(CurrencyRequestDTO::getName, Currency::setFullName);
    }

    public static Currency convertToEntity(CurrencyRequestDTO currencyRequestDTO) {
        return MODEL_MAPPER.map(currencyRequestDTO, Currency.class);
    }

    public static CurrencyResponseDTO convertToDTO(Currency currency) {
        return MODEL_MAPPER.map(currency, CurrencyResponseDTO.class);
    }

    public static ExchangeRateResponseDTO convertToDTO(ExchangeRate exchangeRate) {
        return MODEL_MAPPER.map(exchangeRate, ExchangeRateResponseDTO.class);
    }
}
