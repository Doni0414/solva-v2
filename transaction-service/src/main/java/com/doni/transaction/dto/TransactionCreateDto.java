package com.doni.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionCreateDto {
        @NotNull(message = "{transaction-api.transactions.create.errors.account_from_is_null}")
        @Min(value = 1, message = "{transaction-api.transactions.create.errors.account_from_is_less_than_min}")
        @Max(value = 9999999999L, message = "{transaction-api.transactions.create.errors.account_from_is_greater_than_max}")
        @JsonProperty("account_from")
        private Long accountFrom;

        @NotNull(message = "{transaction-api.transactions.create.errors.account_to_is_null}")
        @Min(value = 1, message = "{transaction-api.transactions.create.errors.account_to_is_less_than_min}")
        @Max(value = 9999999999L, message = "{transaction-api.transactions.create.errors.account_to_is_greater_than_max}")
        @JsonProperty("account_to")
        private Long accountTo;

        @NotNull(message = "{transaction-api.transactions.create.errors.currency_shortname_is_null}")
        @NotBlank(message = "{transaction-api.transactions.create.errors.currency_shortname_is_blank}")
        @Size(min = 3, max = 3, message = "{transaction-api.transactions.create.errors.currency_shortname_has_invalid_size}")
        @JsonProperty("currency_shortname")
        private String currencyShortname;

        @NotNull(message = "{transaction-api.transactions.create.errors.sum_is_null}")
        @Min(value = 0, message = "{transaction-api.transactions.create.errors.sum_is_less_than_min}")
        @JsonProperty("sum")
        private Double sum;

        @NotNull(message = "{transaction-api.transactions.create.errors.expense_category_is_null}")
        @NotBlank(message = "{transaction-api.transactions.create.errors.expense_category_is_blank}")
        @JsonProperty("expense_category")
        private String expenseCategory;

        @NotNull(message = "{transaction-api.transactions.create.errors.datetime_is_null}")
        @NotBlank(message = "{transaction-api.transactions.create.errors.datetime_is_blank}")
        @JsonProperty("datetime")
        private String dateTime;

        public TransactionCreateDto(Long accountFrom, Long accountTo, String currencyShortname, Double sum, String expenseCategory, String dateTime) {
                setAccountFrom(accountFrom);
                setAccountTo(accountTo);
                setCurrencyShortname(currencyShortname);
                setSum(sum);
                setExpenseCategory(expenseCategory);
                setDateTime(dateTime);
        }
}
