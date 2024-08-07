package com.doni.transaction.service;

import com.doni.transaction.client.CurrencyConversionFeignClient;
import com.doni.transaction.dto.OpenExchangeRatesReadDto;
import com.doni.transaction.entity.Currency;
import com.doni.transaction.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultCurrencyService implements CurrencyService {
    @Value("${openexchangerates.api-key}")
    private String apiKey;
    private final CurrencyRepository currencyRepository;
    private final CurrencyConversionFeignClient currencyConversionFeignClient;

    @Override
    public List<Currency> findCurrencies() {
        return currencyRepository.findAll();
    }

    @Override
    @Transactional
    public void updateCurrencyConversion() {
        currencyRepository.deleteAll();
        OpenExchangeRatesReadDto openExchangeRatesReadDto = currencyConversionFeignClient.getCurrencyConversion(apiKey);
        for (Map.Entry<String, Double> entry: openExchangeRatesReadDto.rates().entrySet()) {
            Currency currency = Currency.builder()
                    .currencyShortname(entry.getKey())
                    .inUSD(1 / entry.getValue())
                    .build();
            currencyRepository.save(currency);
        }
    }
}
