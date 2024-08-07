package com.doni.transaction.service;

import com.doni.transaction.entity.Currency;

import java.util.List;

public interface CurrencyService {

    List<Currency> findCurrencies();

    void updateCurrencyConversion();
}
