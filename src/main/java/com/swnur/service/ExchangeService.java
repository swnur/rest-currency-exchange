package com.swnur.service;

import com.swnur.dao.ExchangeRateDAO;
import com.swnur.dao.ExchangeRateDAOImpl;
import com.swnur.dto.ExchangeRequestDTO;
import com.swnur.dto.ExchangeResponseDTO;
import com.swnur.entity.ExchangeRate;
import com.swnur.exception.NotFoundException;
import com.swnur.utils.MappingUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;

import static java.math.MathContext.DECIMAL64;

public class ExchangeService {

    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl();
    private static final int EXCHANGE_RATE_SCALE = 6;
    private static final String DEFAULT_CROSS_BASE_CURRENCY = "USD";

    public ExchangeResponseDTO exchange(ExchangeRequestDTO exchangeRequestDTO) {
        ExchangeRate exchangeRate = findExchangeRate(exchangeRequestDTO)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Exchange rate '%s' - '%s' not found in the database",
                                exchangeRequestDTO.getBaseCurrencyCode(), exchangeRequestDTO.getTargetCurrencyCode()))
                );

        BigDecimal amount = exchangeRequestDTO.getAmount();
        BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate()).setScale(2, RoundingMode.HALF_EVEN);

        return new ExchangeResponseDTO(
                MappingUtils.convertToDTO(exchangeRate.getBaseCurrency()),
                MappingUtils.convertToDTO(exchangeRate.getTargetCurrency()),
                exchangeRate.getRate(),
                exchangeRequestDTO.getAmount(),
                convertedAmount
        );
    }

    private Optional<ExchangeRate> findExchangeRate(ExchangeRequestDTO exchangeRequestDTO) {
        Optional<ExchangeRate> exchangeRate = findByDirectRate(exchangeRequestDTO);

        if (exchangeRate.isEmpty()) {
            exchangeRate = findByIndirectRate(exchangeRequestDTO);
        }

        if (exchangeRate.isEmpty()) {
            exchangeRate = findByCrossRate(exchangeRequestDTO);
        }

        return exchangeRate;
    }

    private Optional<ExchangeRate> findByDirectRate(ExchangeRequestDTO exchangeRequestDTO) {
        return exchangeRateDAO.findByCodes(exchangeRequestDTO.getBaseCurrencyCode(), exchangeRequestDTO.getTargetCurrencyCode());
    }

    private Optional<ExchangeRate> findByIndirectRate(ExchangeRequestDTO exchangeRequestDTO) {
        Optional<ExchangeRate> exchangeRateOptional = exchangeRateDAO.findByCodes(exchangeRequestDTO.getTargetCurrencyCode(), exchangeRequestDTO.getBaseCurrencyCode());

        if (exchangeRateOptional.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate exchangeRate = exchangeRateOptional.get();

        BigDecimal rate = BigDecimal.ONE.divide(exchangeRate.getRate(), MathContext.DECIMAL64)
                .setScale(EXCHANGE_RATE_SCALE, RoundingMode.HALF_EVEN);

        ExchangeRate directExchangeRate = new ExchangeRate(
                exchangeRate.getTargetCurrency(),
                exchangeRate.getBaseCurrency(),
                rate
        );

        return Optional.of(directExchangeRate);
    }

    private Optional<ExchangeRate> findByCrossRate(ExchangeRequestDTO exchangeRequestDTO) {
        Optional<ExchangeRate> usdToBaseOptional = exchangeRateDAO.findByCodes(
                DEFAULT_CROSS_BASE_CURRENCY, exchangeRequestDTO.getBaseCurrencyCode());
        Optional<ExchangeRate> usdToTargetOptional = exchangeRateDAO.findByCodes(
                DEFAULT_CROSS_BASE_CURRENCY, exchangeRequestDTO.getTargetCurrencyCode());


        if (usdToBaseOptional.isEmpty() || usdToTargetOptional.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate usdToBase = usdToBaseOptional.get();
        ExchangeRate usdToTarget = usdToTargetOptional.get();

        BigDecimal rate = usdToTarget.getRate().divide(usdToBase.getRate(), DECIMAL64)
                .setScale(EXCHANGE_RATE_SCALE, RoundingMode.HALF_EVEN);

        ExchangeRate directExchangeRate = new ExchangeRate(
                usdToBase.getTargetCurrency(),
                usdToTarget.getTargetCurrency(),
                rate
        );

        return Optional.of(directExchangeRate);
    }
}
