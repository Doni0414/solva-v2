package com.doni.transaction.mapper;

import com.doni.transaction.dto.TransactionCreateDto;
import com.doni.transaction.dto.TransactionReadDto;
import com.doni.transaction.entity.ExpenseCategory;
import com.doni.transaction.entity.MonthlyLimit;
import com.doni.transaction.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperTest {

    TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    void transactionCreateDtoToTransaction() {
        // given
        TransactionCreateDto transactionCreateDto = new TransactionCreateDto(123L, 9999999999L,
                "KZT", 2000D,
                "service", "2024-01-03 00:01:03 +06");

        // when
        Transaction transaction = transactionMapper.transactionCreateDtoToTransaction(transactionCreateDto);

        // then
        assertEquals(123L, transaction.getAccountFrom());
        assertEquals(9999999999L, transaction.getAccountTo());
        assertEquals("KZT", transaction.getCurrencyShortname());
        assertEquals(2000D, transaction.getSum());
        assertEquals(ExpenseCategory.SERVICE, transaction.getExpenseCategory());
        assertEquals(ZonedDateTime.parse("2024-01-03T00:01:03+06"), transaction.getDateTime());
    }

    @Test
    void transactionToTransactionReadDto() {
        Transaction transaction = Transaction.builder()
                .accountTo(9999999L)
                .accountFrom(123L)
                .sum(1000D)
                .currencyShortname("USD")
                .limitExceeded(false)
                .expenseCategory(ExpenseCategory.SERVICE)
                .dateTime(ZonedDateTime.parse("2024-01-03T00:01:03+06"))
                .build();

        TransactionReadDto actual = transactionMapper.transactionToTransactionReadDto(transaction);

        assertEquals(123L, actual.accountFrom());
        assertEquals(9999999L, actual.accountTo());
        assertEquals(1000D, actual.sum());
        assertEquals("USD", actual.currencyShortname());
        assertEquals(ExpenseCategory.SERVICE, actual.expenseCategory());
        assertEquals("2024-01-03T00:01:03+06:00", actual.dateTime());
        assertEquals(false, actual.limitExceeded());
    }

    @Test
    void transactionsToTransactionReadDtos() {
        List<Transaction> transactions = List.of(
                Transaction.builder()
                        .accountTo(9999999L)
                        .accountFrom(123L)
                        .sum(1000D)
                        .currencyShortname("USD")
                        .limitExceeded(false)
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .dateTime(ZonedDateTime.parse("2024-01-03T00:01:03+06"))
                        .monthlyLimit(MonthlyLimit.builder()
                                .limitSum(2000D)
                                .amount(2000D)
                                .currencyShortname("USD")
                                .expenseCategory(ExpenseCategory.SERVICE)
                                .accountFrom(123L)
                                .dateTime(ZonedDateTime.parse("2024-01-01T00:01:03+06"))
                                .build())
                        .build(),
                Transaction.builder()
                        .accountTo(1000L)
                        .accountFrom(1L)
                        .sum(1000D)
                        .currencyShortname("USD")
                        .limitExceeded(true)
                        .expenseCategory(ExpenseCategory.PRODUCT)
                        .dateTime(ZonedDateTime.parse("2024-01-03T00:01:03+06"))
                        .monthlyLimit(MonthlyLimit.builder()
                                .limitSum(2000D)
                                .amount(2000D)
                                .currencyShortname("USD")
                                .expenseCategory(ExpenseCategory.PRODUCT)
                                .accountFrom(1L)
                                .dateTime(ZonedDateTime.parse("2024-01-01T00:01:03+06"))
                                .build())
                        .build()
        );

        List<TransactionReadDto> expected = transactions.stream()
                .map(transaction -> new TransactionReadDto(transaction.getAccountFrom(), transaction.getAccountTo(),
                        transaction.getCurrencyShortname(), transaction.getSum(), transaction.getExpenseCategory(),
                        transaction.getDateTime().toString(), transaction.getLimitExceeded(), transaction.getMonthlyLimit().getLimitSum(),
                        transaction.getMonthlyLimit().getDateTime().toString(), transaction.getMonthlyLimit().getCurrencyShortname()))
                .toList();

        List<TransactionReadDto> actual = transactionMapper.transactionsToTransactionReadDtos(transactions);

        assertEquals(expected, actual);
    }
}