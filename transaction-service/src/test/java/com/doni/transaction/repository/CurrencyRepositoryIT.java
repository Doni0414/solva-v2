package com.doni.transaction.repository;

import com.doni.transaction.entity.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CurrencyRepositoryIT {

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    void findByCurrencyShortname() {
        Optional<Currency> optionalCurrency = currencyRepository.findByCurrencyShortname("KZT");

        assertTrue(optionalCurrency.isPresent());
        optionalCurrency.ifPresent(currency -> {
            assertEquals("KZT", currency.getCurrencyShortname());
            assertEquals(0.0021, currency.getInUSD());
        });
    }

    @Test
    void findByCurrencyShortname_DoesNotExist() {
        Optional<Currency> optionalCurrency = currencyRepository.findByCurrencyShortname("JPY");

        assertFalse(optionalCurrency.isPresent());
    }
}