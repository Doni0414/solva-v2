package com.doni.transaction.service;

import com.doni.transaction.dto.MonthlyLimitCreateDto;
import com.doni.transaction.entity.ExpenseCategory;
import com.doni.transaction.entity.MonthlyLimit;
import com.doni.transaction.mapper.MonthlyLimitMapper;
import com.doni.transaction.repository.MonthlyLimitRepository;
import com.doni.transaction.utils.CurrencyConversionUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DefaultMonthlyLimitServiceTest {

    @Mock
    MonthlyLimitMapper monthlyLimitMapper;

    @Mock
    MonthlyLimitRepository monthlyLimitRepository;

    @Mock
    CurrencyConversionUtility currencyConversionUtility;

    @InjectMocks
    DefaultMonthlyLimitService monthlyLimitService;

    @Test
    void findMonthlyLimits() {
        // given
        List<MonthlyLimit> expected = List.of(
                MonthlyLimit.builder()
                        .id(5)
                        .amount(1000D)
                        .limitSum(1000D)
                        .dateTime(ZonedDateTime.parse("2003-04-12T04:05:06+06"))
                        .currencyShortname("USD")
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .accountFrom(1L)
                        .build(),
                MonthlyLimit.builder()
                        .id(6)
                        .amount(1000D)
                        .limitSum(1000D)
                        .dateTime(ZonedDateTime.parse("2024-04-12T04:05:06+06"))
                        .currencyShortname("USD")
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .accountFrom(1L)
                        .build(),
                MonthlyLimit.builder()
                        .id(7)
                        .amount(1000D)
                        .limitSum(1000D)
                        .dateTime(ZonedDateTime.parse("2023-04-12T04:05:06+06"))
                        .currencyShortname("USD")
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .accountFrom(2L)
                        .build(),
                MonthlyLimit.builder()
                        .id(8)
                        .amount(500D)
                        .limitSum(1000D)
                        .dateTime(ZonedDateTime.parse("2003-04-12T04:05:06+06"))
                        .currencyShortname("USD")
                        .expenseCategory(ExpenseCategory.PRODUCT)
                        .accountFrom(1L)
                        .build()
        );
        doReturn(expected).when(monthlyLimitRepository).findAll();

        // when
        List<MonthlyLimit> actual = monthlyLimitService.findMonthlyLimits();

        // then
        assertEquals(expected, actual);
    }

    @Test
    void createMonthlyLimit() {
        // given
        MonthlyLimitCreateDto dto = new MonthlyLimitCreateDto();
        dto.setLimitSum(1000D);
        dto.setCurrencyShortname("USD");
        dto.setAccountFrom(1L);
        dto.setDateTime("2003-05-12 04:05:06+06");
        dto.setExpenseCategory("service");

        doReturn(MonthlyLimit.builder()
                .limitSum(700D)
                .amount(700D)
                .expenseCategory(ExpenseCategory.SERVICE)
                .accountFrom(1L)
                .dateTime(ZonedDateTime.parse("2003-05-12T04:05:06+06"))
                .currencyShortname("USD")
                .build()
        ).when(monthlyLimitMapper).monthlyLimitCreateDtoToMonthlyLimit(dto);

        doReturn(Optional.of(MonthlyLimit.builder()
                .id(8)
                .amount(500D)
                .limitSum(1000D)
                .dateTime(ZonedDateTime.parse("2003-04-12T04:05:06+06"))
                .currencyShortname("USD")
                .expenseCategory(ExpenseCategory.SERVICE)
                .accountFrom(1L)
                .build())
        ).when(monthlyLimitRepository).findByExpenseCategoryAndAccountFromOrderByDateTime(ExpenseCategory.SERVICE, 1L, ZonedDateTime.parse("2003-05-12T04:05:06+06"));

        doReturn(1000D)
                .when(currencyConversionUtility).convertToUSD("USD", 1000D);

        doReturn(500D)
                .when(currencyConversionUtility).convertToUSD("USD", 500D);

        doReturn(700D)
                .when(currencyConversionUtility).convertToUSD("USD", 700D);

        doReturn(200D)
                .when(currencyConversionUtility).convertFromUSD("USD", 200D);

        MonthlyLimit expected = MonthlyLimit.builder()
                .id(1)
                .limitSum(700D)
                .amount(700D)
                .expenseCategory(ExpenseCategory.SERVICE)
                .accountFrom(1L)
                .dateTime(ZonedDateTime.parse("2003-05-12T04:05:06+06"))
                .currencyShortname("USD")
                .build();

        doReturn(expected)
                .when(monthlyLimitRepository)
                .save(MonthlyLimit.builder()
                        .limitSum(700D)
                        .amount(200D)
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .accountFrom(1L)
                        .dateTime(ZonedDateTime.parse("2003-05-12T04:05:06+06"))
                        .currencyShortname("USD")
                        .build());

        // when
        MonthlyLimit actual = monthlyLimitService.createMonthlyLimit(dto);

        // then
        assertEquals(expected, actual);
    }
}