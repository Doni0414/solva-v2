package com.doni.transaction.mapper;

import com.doni.transaction.dto.MonthlyLimitCreateDto;
import com.doni.transaction.dto.MonthlyLimitReadDto;
import com.doni.transaction.entity.ExpenseCategory;
import com.doni.transaction.entity.MonthlyLimit;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;


class MonthlyLimitMapperTest {

    MonthlyLimitMapper monthlyLimitMapper = Mappers.getMapper(MonthlyLimitMapper.class);

    @Test
    void monthlyLimitCreateDtoToMonthlyLimit() {
        MonthlyLimitCreateDto dto = new MonthlyLimitCreateDto();
        dto.setLimitSum(1000D);
        dto.setExpenseCategory("service");
        dto.setDateTime("2022-01-02 00:00:00 +06");
        dto.setCurrencyShortname("USD");
        dto.setAccountFrom(123456789L);

        MonthlyLimit actual = monthlyLimitMapper.monthlyLimitCreateDtoToMonthlyLimit(dto);

        assertEquals(1000D, actual.getLimitSum());
        assertEquals(1000D, actual.getAmount());
        assertEquals(ExpenseCategory.SERVICE, actual.getExpenseCategory());
        assertEquals("USD", actual.getCurrencyShortname());
        assertEquals(ZonedDateTime.parse("2022-01-02T00:00:00+06"), actual.getDateTime());
        assertEquals(123456789L, actual.getAccountFrom());
    }

    @Test
    void monthlyLimitToMonthlyLimitReadDto() {
        MonthlyLimit monthlyLimit = MonthlyLimit.builder()
                .limitSum(1000D)
                .amount(1000D)
                .currencyShortname("USD")
                .expenseCategory(ExpenseCategory.SERVICE)
                .dateTime(ZonedDateTime.parse("2022-01-02T00:00:00+06"))
                .accountFrom(123456789L)
                .build();

        MonthlyLimitReadDto actual = monthlyLimitMapper.monthlyLimitToMonthlyLimitReadDto(monthlyLimit);

        assertEquals(1000D, actual.limitSum());
        assertEquals("USD", actual.currencyShortname());
        assertEquals("SERVICE", actual.expenseCategory());
        assertEquals("2022-01-02T00:00:00+06:00", actual.dateTime());
        assertEquals(123456789L, actual.accountFrom());
    }
}