package com.doni.transaction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "transaction", name = "t_transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account_from")
    private Long accountFrom;

    @Column(name = "account_to")
    private Long accountTo;

    @Column(name = "currency_shortname")
    private String currencyShortname;

    @Column(name = "sum")
    private Double sum;

    @Column(name = "expense_category")
    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;

    @Column(name = "datetime")
    private ZonedDateTime dateTime;

    @Column(name = "limit_exceeded")
    private Boolean limitExceeded;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MonthlyLimit monthlyLimit;

}
