package com.swnur.dao;

import com.swnur.entity.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDAO extends CrudDAO<ExchangeRate, Long> {

    Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode);
}
