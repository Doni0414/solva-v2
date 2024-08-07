package com.doni.transaction.dto;

import com.doni.transaction.entity.ExpenseCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record TransactionReadDto(
        @JsonProperty("account_from")
        Long accountFrom,

        @JsonProperty("account_to")
        Long accountTo,

        @JsonProperty("currency_shortname")
        String currencyShortname,

        @JsonProperty("sum")
        Double sum,

        @JsonProperty("expense_category")
        ExpenseCategory expenseCategory,

        @JsonProperty("datetime")
        String dateTime,

        @JsonProperty("limit_exceeded")
        Boolean limitExceeded,

        @JsonProperty("limit_sum")
        Double limitSum,

        @JsonProperty("limit_datetime")
        String limitDateTime,

        @JsonProperty("limit_currency_shortname")
        String limitCurrencyShortname) {
}
