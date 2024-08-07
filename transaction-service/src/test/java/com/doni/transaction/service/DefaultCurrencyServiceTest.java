package com.doni.transaction.service;

import com.doni.transaction.client.CurrencyConversionFeignClient;
import com.doni.transaction.dto.OpenExchangeRatesReadDto;
import com.doni.transaction.entity.Currency;
import com.doni.transaction.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCurrencyServiceTest {

    @Mock
    CurrencyRepository currencyRepository;

    @Mock
    CurrencyConversionFeignClient currencyConversionFeignClient;

    @InjectMocks
    DefaultCurrencyService currencyService;

    @Test
    void findCurrencies() {
        doReturn(List.of(
                new Currency(1, "KZT", 0.0021),
                new Currency(2, "RUB", 0.0208)
        )).when(currencyRepository).findAll();

        List<Currency> actual = currencyService.findCurrencies();

        assertEquals(List.of(
                new Currency(1, "KZT", 0.0021),
                new Currency(2, "RUB", 0.0208)
        ), actual);

        verify(currencyRepository).findAll();
        verifyNoMoreInteractions(currencyRepository);
    }

    @Test
    void updateCurrencyConversion() {
        doReturn(new OpenExchangeRatesReadDto("USD", Map.of("KZT", 0.0021, "RUB", 0.018)))
                .when(currencyConversionFeignClient).getCurrencyConversion(null);

        currencyService.updateCurrencyConversion();

        verify(currencyRepository).deleteAll();
        verify(currencyRepository, times(2)).save(any());
        verifyNoMoreInteractions(currencyRepository);
    }
}