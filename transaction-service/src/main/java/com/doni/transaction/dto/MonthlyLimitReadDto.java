package com.doni.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MonthlyLimitReadDto(
        @JsonProperty("limit_sum")
        Double limitSum,

        @JsonProperty("limit_datetime")
        String dateTime,

        @JsonProperty("limit_currency_shortname")
        String currencyShortname,

        @JsonProperty("limit_expense_category")
        String expenseCategory,

        @JsonProperty("account_from")
        Long accountFrom) {
}
