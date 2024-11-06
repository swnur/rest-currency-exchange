package com.swnur.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeResponseDTO {

    private CurrencyResponseDTO baseCurrency;

    private CurrencyResponseDTO targetCurrency;

    private BigDecimal rate;

    private BigDecimal amount;

    private BigDecimal convertedAmount;

}
