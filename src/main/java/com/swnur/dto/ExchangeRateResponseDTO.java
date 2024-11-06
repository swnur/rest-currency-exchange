package com.swnur.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateResponseDTO {

    private Long id;

    private CurrencyResponseDTO baseCurrency;

    private CurrencyResponseDTO targetCurrency;

    private BigDecimal rate;

    public ExchangeRateResponseDTO(CurrencyResponseDTO baseCurrency, CurrencyResponseDTO targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency =  targetCurrency;
        this.rate = rate;
    }
}
