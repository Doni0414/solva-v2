package com.doni.transaction.repository;

import com.doni.transaction.entity.ExpenseCategory;
import com.doni.transaction.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TransactionRepositoryIT {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void save_FieldsAreValid_TransactionIsCreated() {
        Transaction transaction = Transaction.builder()
                .accountFrom(123L)
                .accountTo(9999999999L)
                .sum(1000D)
                .currencyShortname("KZT")
                .expenseCategory(ExpenseCategory.SERVICE)
                .dateTime(ZonedDateTime.parse("2024-01-03T01:12:03+06"))
                .build();

        Transaction insertedTransaction = transactionRepository.save(transaction);

        Transaction actual = entityManager.find(Transaction.class, insertedTransaction.getId());
        assertEquals(transaction, actual);
    }

    @Test
    void save_FieldsAreInvalid_SumIsNull_ThrowsDataIntegrityViolationException() {
        Transaction transaction = Transaction.builder()
                .accountFrom(123L)
                .accountTo(9999999999L)
                .currencyShortname("KZT")
                .expenseCategory(ExpenseCategory.SERVICE)
                .dateTime(ZonedDateTime.parse("2024-01-03T01:12:03+06"))
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> transactionRepository.save(transaction));
    }

    @Test
    void save_FieldsAreInvalid_SumIsNegative_ThrowsDataIntegrityViolationException() {
        Transaction transaction = Transaction.builder()
                .accountFrom(123L)
                .accountTo(9999999999L)
                .currencyShortname("KZT")
                .sum(-1000D)
                .expenseCategory(ExpenseCategory.SERVICE)
                .dateTime(ZonedDateTime.parse("2024-01-03T01:12:03+06"))
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> transactionRepository.save(transaction));
    }

    @Test
    @Sql(scripts = {"/sql/monthly_limits.sql", "/sql/transactions.sql"})
    void findAllByLimitExceededTrue() {
        List<Transaction> transactions = transactionRepository.findAllByLimitExceeded(true);
        transactions.forEach(transaction -> transaction.setMonthlyLimit(null));

        List<Transaction> expected = List.of(
                Transaction.builder()
                        .id(2)
                        .accountFrom(123L)
                        .accountTo(1001L)
                        .currencyShortname("USD")
                        .sum(100D)
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .dateTime(ZonedDateTime.parse("2003-04-12T04:05:06+06"))
                        .limitExceeded(true)
                        .build(),
                Transaction.builder()
                        .id(3)
                        .accountFrom(123L)
                        .accountTo(1002L)
                        .currencyShortname("USD")
                        .sum(100D)
                        .expenseCategory(ExpenseCategory.PRODUCT)
                        .dateTime(ZonedDateTime.parse("2003-04-12T04:05:06+06"))
                        .limitExceeded(true)
                        .build()
        );
        assertEquals(expected, transactions);
    }

    @Test
    @Sql(scripts = {"/sql/monthly_limits.sql", "/sql/transactions.sql"})
    void findAllByLimitExceededFalse() {
        List<Transaction> transactions = transactionRepository.findAllByLimitExceeded(false);
        transactions.forEach(transaction -> transaction.setMonthlyLimit(null));

        List<Transaction> expected = List.of(
                Transaction.builder()
                        .id(1)
                        .accountFrom(123L)
                        .accountTo(1000L)
                        .currencyShortname("USD")
                        .sum(100D)
                        .expenseCategory(ExpenseCategory.SERVICE)
                        .dateTime(ZonedDateTime.parse("2003-04-12T04:05:06+06"))
                        .limitExceeded(false)
                        .build()
        );
        assertEquals(expected, transactions);
    }
}