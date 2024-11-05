package com.swnur.dto;

import java.math.BigDecimal;

public class ExchangeResponseDTO {

    private Long id;

    private CurrencyResponseDTO baseCurrency;

    private CurrencyResponseDTO targetCurrency;

    private BigDecimal rate;
}
