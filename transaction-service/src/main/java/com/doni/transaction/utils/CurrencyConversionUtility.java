package com.doni.transaction.utils;

import com.doni.transaction.entity.Currency;
import com.doni.transaction.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurrencyConversionUtility {
    private final CurrencyRepository currencyRepository;

    public double convertToUSD(String currencyShortname, double amount) {
        if (currencyShortname.equals("USD")) {
            return amount;
        } else {
            Optional<Currency> optionalCurrency = currencyRepository.findByCurrencyShortname(currencyShortname);
            return optionalCurrency.map(currency -> currency.getInUSD() * amount).orElse(0.0);
        }
    }

    public double convertFromUSD(String currencyShortname, double amountInUSD) {
        if (currencyShortname.equals("USD")) {
            return amountInUSD;
        } else {
            Optional<Currency> optionalCurrency = currencyRepository.findByCurrencyShortname(currencyShortname);
            return optionalCurrency.map(currency -> (1 / currency.getInUSD()) * amountInUSD).orElse(0.0);
        }
    }
}
