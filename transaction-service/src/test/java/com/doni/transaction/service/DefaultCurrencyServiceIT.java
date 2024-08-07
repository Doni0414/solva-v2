package com.doni.transaction.service;

import com.doni.transaction.entity.Currency;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DefaultCurrencyServiceIT {

    @Value("${openexchangerates.api-key}")
    String apiKey;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    EntityManager entityManager;

    @Test
    void findCurrencies() {
        List<Currency> actual = currencyService.findCurrencies();

        List<Currency> expected = entityManager
                .createQuery("select c from Currency c", Currency.class)
                .getResultList();

        assertEquals(expected, actual);
    }

    @Test
    void updateCurrencyConversion() {
        currencyService.updateCurrencyConversion();

        List<Currency> actual = entityManager
                .createQuery("select c from Currency c", Currency.class)
                .getResultList();

        assertTrue(actual.size() > 2);
    }
}