package com.doni.transaction.service;

import com.doni.transaction.dto.MonthlyLimitReadDto;
import com.doni.transaction.dto.TransactionCreateDto;
import com.doni.transaction.dto.TransactionReadDto;
import com.doni.transaction.entity.ExpenseCategory;
import com.doni.transaction.entity.MonthlyLimit;
import com.doni.transaction.entity.Transaction;
import com.doni.transaction.mapper.TransactionMapper;
import com.doni.transaction.repository.MonthlyLimitRepository;
import com.doni.transaction.repository.TransactionRepository;
import com.doni.transaction.utils.CurrencyConversionUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DefaultTransactionServiceTest {

    @Mock
    TransactionMapper transactionMapper;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    MonthlyLimitRepository monthlyLimitRepository;

    @Mock
    CurrencyConversionUtility currencyConversionUtility;

    @InjectMocks
    DefaultTransactionService transactionService;

    @Test
    void findTransactionsWithLimitExceeded() {
        //given
        TransactionReadDto dto1 = new TransactionReadDto(1L, 3L,
                "USD", 100D, ExpenseCategory.SERVICE,
                "2003-04-12T04:05:06+06", true,
                1000D, "2003-02-12T04:05:06+06", "USD");

        TransactionReadDto dto2 = new TransactionReadDto(1L, 4L,
                "USD", 200D, ExpenseCategory.SERVICE,
                "2003-04-12T04:05:06+06", true,
                1000D, "2003-02-12T04:05:06+06", "USD");

        List<TransactionReadDto> expected = List.of(
                dto1,
                dto2
        );

        List<Transaction> transactions = List.of(
                Transaction.builder()
                        .id(1)
                        .accountFrom(1L)
                        .accountTo(3L)
                        .currencyShortname("USD")
                        .sum(100D)
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .dateTime(ZonedDateTime.parse("2003-04-12T04:05:06+06"))
                        .limitExceeded(true)
                        .build(),
                Transaction.builder()
                        .id(2)
                        .accountFrom(1L)
                        .accountTo(4L)
                        .currencyShortname("USD")
                        .sum(200D)
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .dateTime(ZonedDateTime.parse("2003-04-12T04:05:06+06"))
                        .limitExceeded(true)
                        .build()
        );

        doReturn(transactions).when(transactionRepository).findAllByLimitExceeded(true);

        doReturn(expected).when(transactionMapper).transactionsToTransactionReadDtos(transactions);
        // when
        List<TransactionReadDto> actual = transactionService.findTransactionsWithLimitExceeded();

        //then
        assertEquals(expected, actual);
    }

    @Test
    void createTransaction_LimitIsNotExceeded() {
        // given
        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setAccountFrom(2L);
        dto.setAccountTo(99999999L);
        dto.setSum(500D);
        dto.setDateTime("2025-01-03 00:00:00 +06");
        dto.setCurrencyShortname("USD");
        dto.setExpenseCategory("service");

        doReturn(Transaction.builder()
                .accountFrom(2L)
                .accountTo(99999999L)
                .sum(500D)
                .expenseCategory(ExpenseCategory.SERVICE)
                .dateTime(ZonedDateTime.parse("2025-01-03T00:00:00+06"))
                .limitExceeded(false)
                .currencyShortname("USD")
                .build()
        ).when(transactionMapper).transactionCreateDtoToTransaction(dto);

        doReturn(Optional.of(MonthlyLimit.builder()
                .accountFrom(2L)
                .currencyShortname("USD")
                .limitSum(1000D)
                .amount(1000D)
                .dateTime(ZonedDateTime.parse("2025-01-01T00:00:00+06"))
                .expenseCategory(ExpenseCategory.SERVICE)
                .build())
        ).when(monthlyLimitRepository).findByExpenseCategoryAndAccountFromOrderByDateTime(ExpenseCategory.SERVICE, 2L, ZonedDateTime.parse("2025-01-03T00:00:00+06"));

        doReturn(1000D)
                .when(currencyConversionUtility).convertToUSD("USD", 1000D);

        doReturn(500D)
                .when(currencyConversionUtility).convertToUSD("USD", 500D);

        doReturn(500D)
                .when(currencyConversionUtility).convertFromUSD("USD", 500D);

        Transaction expected = Transaction.builder()
                .id(1)
                .accountFrom(2L)
                .accountTo(99999999L)
                .currencyShortname("USD")
                .sum(500D)
                .expenseCategory(ExpenseCategory.SERVICE)
                .dateTime(ZonedDateTime.parse("2025-01-03T00:00:00+06"))
                .limitExceeded(false)
                .build();

        doReturn(expected)
                .when(transactionRepository).save(Transaction.builder()
                .accountFrom(2L)
                .accountTo(99999999L)
                .sum(500D)
                .expenseCategory(ExpenseCategory.SERVICE)
                .dateTime(ZonedDateTime.parse("2025-01-03T00:00:00+06"))
                .limitExceeded(false)
                .currencyShortname("USD")
                .build());

        // when
        Transaction transaction = transactionService.createTransaction(dto);

        // then
        assertEquals(expected, transaction);
    }

    @Test
    void createTransaction_LimitIsExceeded() {
        // given
        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setAccountFrom(2L);
        dto.setAccountTo(99999999L);
        dto.setSum(500D);
        dto.setDateTime("2025-01-03 00:00:00 +06");
        dto.setCurrencyShortname("USD");
        dto.setExpenseCategory("service");

        doReturn(Transaction.builder()
                .accountFrom(2L)
                .accountTo(99999999L)
                .sum(500D)
                .expenseCategory(ExpenseCategory.SERVICE)
                .dateTime(ZonedDateTime.parse("2025-01-03T00:00:00+06"))
                .limitExceeded(false)
                .currencyShortname("USD")
                .build()
        ).when(transactionMapper).transactionCreateDtoToTransaction(dto);

        doReturn(Optional.of(MonthlyLimit.builder()
                .accountFrom(2L)
                .currencyShortname("USD")
                .limitSum(1000D)
                .amount(300D)
                .dateTime(ZonedDateTime.parse("2025-01-01T00:00:00+06"))
                .expenseCategory(ExpenseCategory.SERVICE)
                .build())
        ).when(monthlyLimitRepository).findByExpenseCategoryAndAccountFromOrderByDateTime(ExpenseCategory.SERVICE, 2L, ZonedDateTime.parse("2025-01-03T00:00:00+06"));

        doReturn(300D)
                .when(currencyConversionUtility).convertToUSD("USD", 300D);

        doReturn(500D)
                .when(currencyConversionUtility).convertToUSD("USD", 500D);

        doReturn(-200D)
                .when(currencyConversionUtility).convertFromUSD("USD", -200D);

        Transaction expected = Transaction.builder()
                .id(1)
                .accountFrom(2L)
                .accountTo(99999999L)
                .currencyShortname("USD")
                .sum(500D)
                .expenseCategory(ExpenseCategory.SERVICE)
                .dateTime(ZonedDateTime.parse("2025-01-03T00:00:00+06"))
                .limitExceeded(true)
                .build();

        doReturn(expected)
                .when(transactionRepository).save(Transaction.builder()
                .accountFrom(2L)
                .accountTo(99999999L)
                .sum(500D)
                .expenseCategory(ExpenseCategory.SERVICE)
                .dateTime(ZonedDateTime.parse("2025-01-03T00:00:00+06"))
                .limitExceeded(true)
                .currencyShortname("USD")
                .build());

        // when
        Transaction transaction = transactionService.createTransaction(dto);

        // then
        assertEquals(expected, transaction);
    }
}