package com.doni.transaction.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonthlyLimitCreateDto {
    @NotNull(message = "{transaction-api.limits.create.errors.limit_sum_is_null}")
    @Min(value = 0, message = "{transaction-api.limits.create.errors.limit_sum_is_less_than_min}")
    @JsonProperty("limit_sum")
    private Double limitSum;

    @JsonProperty("limit_datetime")
    @NotNull(message = "{transaction-api.limits.create.errors.datetime_is_null}")
    @NotBlank(message = "{transaction-api.limits.create.errors.datetime_is_blank}")
    private String dateTime;

    @JsonProperty("limit_currency_shortname")
    @NotNull(message = "{transaction-api.limits.create.errors.currency_shortname_is_null}")
    @NotBlank(message = "{transaction-api.limits.create.errors.currency_shortname_is_blank}")
    private String currencyShortname;

    @JsonProperty("limit_expense_category")
    @NotNull(message = "{transaction-api.limits.create.errors.expense_category_is_null}")
    @NotBlank(message = "{transaction-api.limits.create.errors.expense_category_is_blank}")
    private String expenseCategory;

    @JsonProperty("account_from")
    @NotNull(message = "{transaction-api.limits.create.errors.account_from_is_null}")
    @Min(value = 1, message = "{transaction-api.limits.create.errors.account_from_less_than_min}")
    private Long accountFrom;

}
