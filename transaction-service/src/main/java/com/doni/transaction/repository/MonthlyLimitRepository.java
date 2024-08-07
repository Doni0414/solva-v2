package com.doni.transaction.repository;

import com.doni.transaction.entity.ExpenseCategory;
import com.doni.transaction.entity.MonthlyLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public interface MonthlyLimitRepository extends JpaRepository<MonthlyLimit, Integer> {

    @Query("select m from MonthlyLimit m " +
            "where m.expenseCategory = ?1 " +
            "and m.accountFrom = ?2 " +
            "and m.dateTime <= ?3 " +
            "order by m.dateTime desc " +
            "limit 1")
    Optional<MonthlyLimit> findByExpenseCategoryAndAccountFromOrderByDateTime(ExpenseCategory expenseCategory, Long accountFrom, ZonedDateTime dateTime);
}
