package com.doni.transaction.repository;

import com.doni.transaction.entity.ExpenseCategory;
import com.doni.transaction.entity.MonthlyLimit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Sql("/sql/monthly_limits.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MonthlyLimitRepositoryIT {

    @Autowired
    MonthlyLimitRepository monthlyLimitRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void findByExpenseCategoryOrderByDateTime1() {
        Optional<MonthlyLimit> optionalMonthlyLimit = monthlyLimitRepository
                .findByExpenseCategoryAndAccountFromOrderByDateTime(ExpenseCategory.SERVICE, 1L, ZonedDateTime.parse("2025-01-02T00:00:00+06"));

        assertTrue(optionalMonthlyLimit.isPresent());
        optionalMonthlyLimit.ifPresent(monthlyLimit -> {
            assertEquals(6, monthlyLimit.getId());
            assertEquals(1L, monthlyLimit.getAccountFrom());
            assertEquals(1000D, monthlyLimit.getLimitSum());
            assertEquals(1000D, monthlyLimit.getAmount());
            assertEquals(ZonedDateTime.parse("2024-04-12T04:05:06+06"), monthlyLimit.getDateTime());
            assertEquals("USD", monthlyLimit.getCurrencyShortname());
            assertEquals(ExpenseCategory.SERVICE, monthlyLimit.getExpenseCategory());
        });
    }

    @Test
    void findByExpenseCategoryOrderByDateTime2() {
        Optional<MonthlyLimit> optionalMonthlyLimit = monthlyLimitRepository
                .findByExpenseCategoryAndAccountFromOrderByDateTime(ExpenseCategory.SERVICE, 1L, ZonedDateTime.parse("2024-02-01T00:00:00+06"));

        assertTrue(optionalMonthlyLimit.isPresent());
        optionalMonthlyLimit.ifPresent(monthlyLimit -> {
            assertEquals(5, monthlyLimit.getId());
            assertEquals(1L, monthlyLimit.getAccountFrom());
            assertEquals(1000D, monthlyLimit.getLimitSum());
            assertEquals(1000D, monthlyLimit.getAmount());
            assertEquals(ZonedDateTime.parse("2003-04-12T04:05:06+06"), monthlyLimit.getDateTime());
            assertEquals("USD", monthlyLimit.getCurrencyShortname());
            assertEquals(ExpenseCategory.SERVICE, monthlyLimit.getExpenseCategory());
        });
    }

    @Test
    void findByExpenseCategoryOrderByDateTime3() {
        Optional<MonthlyLimit> optionalMonthlyLimit = monthlyLimitRepository
                .findByExpenseCategoryAndAccountFromOrderByDateTime(ExpenseCategory.PRODUCT, 1L, ZonedDateTime.parse("2024-02-01T00:00:00+06"));

        assertTrue(optionalMonthlyLimit.isPresent());
        optionalMonthlyLimit.ifPresent(monthlyLimit -> {
            assertEquals(8, monthlyLimit.getId());
            assertEquals(1L, monthlyLimit.getAccountFrom());
            assertEquals(1000D, monthlyLimit.getLimitSum());
            assertEquals(500D, monthlyLimit.getAmount());
            assertEquals(ZonedDateTime.parse("2003-04-12T04:05:06+06"), monthlyLimit.getDateTime());
            assertEquals("USD", monthlyLimit.getCurrencyShortname());
            assertEquals(ExpenseCategory.PRODUCT, monthlyLimit.getExpenseCategory());
        });
    }

    @Test
    void findByExpenseCategoryOrderByDateTime4() {
        Optional<MonthlyLimit> optionalMonthlyLimit = monthlyLimitRepository
                .findByExpenseCategoryAndAccountFromOrderByDateTime(ExpenseCategory.SERVICE, 2L, ZonedDateTime.parse("2024-02-01T00:00:00+06"));

        assertTrue(optionalMonthlyLimit.isPresent());
        optionalMonthlyLimit.ifPresent(monthlyLimit -> {
            assertEquals(7, monthlyLimit.getId());
            assertEquals(2L, monthlyLimit.getAccountFrom());
            assertEquals(1000D, monthlyLimit.getLimitSum());
            assertEquals(1000D, monthlyLimit.getAmount());
            assertEquals(ZonedDateTime.parse("2023-04-12T04:05:06+06"), monthlyLimit.getDateTime());
            assertEquals("USD", monthlyLimit.getCurrencyShortname());
            assertEquals(ExpenseCategory.SERVICE, monthlyLimit.getExpenseCategory());
        });
    }
}