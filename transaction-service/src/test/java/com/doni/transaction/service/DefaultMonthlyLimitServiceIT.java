package com.doni.transaction.service;

import com.doni.transaction.dto.MonthlyLimitCreateDto;
import com.doni.transaction.entity.ExpenseCategory;
import com.doni.transaction.entity.MonthlyLimit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@Sql("/sql/monthly_limits.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DefaultMonthlyLimitServiceIT {

    @Autowired
    MonthlyLimitService monthlyLimitService;

    @Test
    void findMonthlyLimits() {
        // when
        List<MonthlyLimit> actual = monthlyLimitService.findMonthlyLimits();

        // then
        assertEquals(List.of(
                MonthlyLimit.builder()
                        .id(5)
                        .amount(1000D)
                        .limitSum(1000D)
                        .dateTime(ZonedDateTime.parse("2003-04-12T04:05:06+06"))
                        .currencyShortname("USD")
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .accountFrom(1L)
                        .transactions(new ArrayList<>())
                        .build(),
                MonthlyLimit.builder()
                        .id(6)
                        .amount(1000D)
                        .limitSum(1000D)
                        .dateTime(ZonedDateTime.parse("2024-04-12T04:05:06+06"))
                        .currencyShortname("USD")
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .accountFrom(1L)
                        .transactions(new ArrayList<>())
                        .build(),
                MonthlyLimit.builder()
                        .id(7)
                        .amount(1000D)
                        .limitSum(1000D)
                        .dateTime(ZonedDateTime.parse("2023-04-12T04:05:06+06"))
                        .currencyShortname("USD")
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .accountFrom(2L)
                        .transactions(new ArrayList<>())
                        .build(),
                MonthlyLimit.builder()
                        .id(8)
                        .amount(500D)
                        .limitSum(1000D)
                        .dateTime(ZonedDateTime.parse("2003-04-12T04:05:06+06"))
                        .currencyShortname("USD")
                        .expenseCategory(ExpenseCategory.PRODUCT)
                        .accountFrom(1L)
                        .transactions(new ArrayList<>())
                        .build()
        ), actual);
    }

    @Test
    void createMonthlyLimit1() {
        // given
        MonthlyLimitCreateDto dto = new MonthlyLimitCreateDto();
        dto.setLimitSum(1000D);
        dto.setCurrencyShortname("USD");
        dto.setAccountFrom(1L);
        dto.setDateTime("2003-05-12 04:05:06+06");
        dto.setExpenseCategory("service");

        // when
        MonthlyLimit actual = monthlyLimitService.createMonthlyLimit(dto);

        // then
        assertEquals(MonthlyLimit.builder()
                .id(1)
                .accountFrom(1L)
                .limitSum(1000D)
                .amount(1000D)
                .dateTime(ZonedDateTime.parse("2003-05-12T04:05:06+06"))
                .currencyShortname("USD")
                .expenseCategory(ExpenseCategory.SERVICE)
                .build(), actual);
    }

    @Test
    void createMonthlyLimit2() {
        // given
        MonthlyLimitCreateDto dto = new MonthlyLimitCreateDto();
        dto.setLimitSum(400D);
        dto.setCurrencyShortname("USD");
        dto.setAccountFrom(1L);
        dto.setDateTime("2003-05-12 04:05:06+06");
        dto.setExpenseCategory("product");

        // when
        MonthlyLimit actual = monthlyLimitService.createMonthlyLimit(dto);

        // then
        assertEquals(MonthlyLimit.builder()
                .id(2)
                .accountFrom(1L)
                .limitSum(400D)
                .amount(-100D)
                .dateTime(ZonedDateTime.parse("2003-05-12T04:05:06+06"))
                .currencyShortname("USD")
                .expenseCategory(ExpenseCategory.PRODUCT)
                .build(), actual);
    }
}