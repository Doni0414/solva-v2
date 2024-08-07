package com.doni.transaction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "transaction", name = "t_monthly_limit")
public class MonthlyLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "limit_sum")
    private Double limitSum;

    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime dateTime;

    @Column(name = "currency_shortname")
    private String currencyShortname;

    @Column(name = "account_from")
    private Long accountFrom;

    @Column(name = "expense_category")
    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;

    @OneToMany(mappedBy = "monthlyLimit")
    private List<Transaction> transactions;

    public void addTransaction(Transaction transaction) {
        transaction.setMonthlyLimit(this);
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        transactions.add(transaction);
    }
}
