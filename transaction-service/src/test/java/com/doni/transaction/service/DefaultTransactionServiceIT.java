package com.doni.transaction.service;

import com.doni.transaction.dto.TransactionCreateDto;
import com.doni.transaction.dto.TransactionReadDto;
import com.doni.transaction.entity.ExpenseCategory;
import com.doni.transaction.entity.MonthlyLimit;
import com.doni.transaction.entity.Transaction;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DefaultTransactionServiceIT {

    @Autowired
    TransactionService transactionService;

    @Autowired
    EntityManager entityManager;

    @Test
    @Sql(scripts = {"/sql/monthly_limits.sql", "/sql/transactions.sql"})
    void findTransactionsWithLimitExceeded() {
        List<TransactionReadDto> actual = transactionService.findTransactionsWithLimitExceeded();

        List<TransactionReadDto> expected = ((List<Transaction>) entityManager
                .createQuery("select t from Transaction t where t.limitExceeded = true")
                .getResultList())
                .stream()
                .map(transaction -> new TransactionReadDto(transaction.getAccountFrom(), transaction.getAccountTo(),
                        transaction.getCurrencyShortname(), transaction.getSum(), transaction.getExpenseCategory(),
                        transaction.getDateTime().toString(), transaction.getLimitExceeded(), transaction.getMonthlyLimit().getLimitSum(),
                        transaction.getMonthlyLimit().getDateTime().toString(), transaction.getMonthlyLimit().getCurrencyShortname()))
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    @Sql("/sql/monthly_limits.sql")
    void createTransaction_LimitIsNotExceeded() {
        // given
        TransactionCreateDto dto1 = new TransactionCreateDto();
        dto1.setAccountFrom(1L);
        dto1.setAccountTo(99999999L);
        dto1.setSum(500D);
        dto1.setDateTime("2025-01-03 00:00:00 +06");
        dto1.setCurrencyShortname("USD");
        dto1.setExpenseCategory("service");

        TransactionCreateDto dto2 = new TransactionCreateDto();
        dto2.setAccountFrom(1L);
        dto2.setAccountTo(99999999L);
        dto2.setSum(500D);
        dto2.setDateTime("2025-01-03 00:00:00 +06");
        dto2.setCurrencyShortname("USD");
        dto2.setExpenseCategory("service");

        // when
        Transaction transaction1 = transactionService.createTransaction(dto1);
        Transaction transaction2 = transactionService.createTransaction(dto2);

        // then
        assertEquals(1L, transaction1.getAccountFrom());
        assertEquals(99999999L, transaction1.getAccountTo());
        assertEquals(500D, transaction1.getSum());
        assertEquals(ZonedDateTime.parse("2025-01-03T00:00:00+06"), transaction1.getDateTime());
        assertEquals("USD", transaction1.getCurrencyShortname());
        assertEquals(ExpenseCategory.SERVICE, transaction1.getExpenseCategory());
        assertEquals(6, transaction1.getMonthlyLimit().getId());
        assertFalse(transaction1.getLimitExceeded());

        assertEquals(1L, transaction2.getAccountFrom());
        assertEquals(99999999L, transaction2.getAccountTo());
        assertEquals(500D, transaction2.getSum());
        assertEquals(ZonedDateTime.parse("2025-01-03T00:00:00+06"), transaction2.getDateTime());
        assertEquals("USD", transaction2.getCurrencyShortname());
        assertEquals(ExpenseCategory.SERVICE, transaction2.getExpenseCategory());
        assertEquals(6, transaction2.getMonthlyLimit().getId());
        assertFalse(transaction2.getLimitExceeded());

        assertEquals(0D, entityManager
                .createQuery("select m from MonthlyLimit m where m.id = 6", MonthlyLimit.class)
                .getSingleResult()
                .getAmount());
    }

    @Test
    @Sql("/sql/monthly_limits.sql")
    void createTransaction_LimitIsExceeded() {
        // given
        TransactionCreateDto dto1 = new TransactionCreateDto();
        dto1.setAccountFrom(2L);
        dto1.setAccountTo(99999999L);
        dto1.setSum(500D);
        dto1.setDateTime("2025-01-03 00:00:00 +06");
        dto1.setCurrencyShortname("USD");
        dto1.setExpenseCategory("service");

        TransactionCreateDto dto2 = new TransactionCreateDto();
        dto2.setAccountFrom(2L);
        dto2.setAccountTo(99999999L);
        dto2.setSum(600D);
        dto2.setDateTime("2025-01-03 00:00:00 +06");
        dto2.setCurrencyShortname("USD");
        dto2.setExpenseCategory("service");

        // when
        Transaction transaction1 = transactionService.createTransaction(dto1);
        Transaction transaction2 = transactionService.createTransaction(dto2);

        // then
        assertEquals(2L, transaction1.getAccountFrom());
        assertEquals(99999999L, transaction1.getAccountTo());
        assertEquals(500D, transaction1.getSum());
        assertEquals(ZonedDateTime.parse("2025-01-03T00:00:00+06"), transaction1.getDateTime());
        assertEquals("USD", transaction1.getCurrencyShortname());
        assertEquals(ExpenseCategory.SERVICE, transaction1.getExpenseCategory());
        assertEquals(7, transaction1.getMonthlyLimit().getId());
        assertFalse(transaction1.getLimitExceeded());

        assertEquals(2L, transaction2.getAccountFrom());
        assertEquals(99999999L, transaction2.getAccountTo());
        assertEquals(600D, transaction2.getSum());
        assertEquals(ZonedDateTime.parse("2025-01-03T00:00:00+06"), transaction2.getDateTime());
        assertEquals("USD", transaction2.getCurrencyShortname());
        assertEquals(ExpenseCategory.SERVICE, transaction2.getExpenseCategory());
        assertEquals(7, transaction2.getMonthlyLimit().getId());
        assertTrue(transaction2.getLimitExceeded());

        assertEquals(-100D, entityManager
                .createQuery("select m from MonthlyLimit m where m.id = 7", MonthlyLimit.class)
                .getSingleResult()
                .getAmount());
    }
}