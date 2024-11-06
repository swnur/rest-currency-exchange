package com.swnur.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateRequestDTO {

    private String baseCurrencyCode;

    private String targetCurrencyCode;

    private BigDecimal rate;
}
