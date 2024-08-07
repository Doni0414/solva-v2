package com.doni.transaction.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LimitsRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void createMonthlyLimit_PayloadIsValid_ReturnsCreated() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/monthly-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "limit_sum": 2000,
                    "limit_datetime": "2024-08-05 01:00:14 +06",
                    "limit_currency_shortname": "USD",
                    "limit_expense_category": "PRODUCT",
                    "account_from": 1
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                        {
                            "limit_sum": 2000,
                            "limit_datetime": "2024-08-05T01:00:14+06:00",
                            "limit_currency_shortname": "USD",
                            "limit_expense_category": "PRODUCT",
                            "account_from": 1
                        }
                        """)
                );
    }

    @Test
    void createMonthlyLimit_PayloadIsInvalid_LimitSumIsNull_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/monthly-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "limit_sum": null,
                    "limit_datetime": "2024-08-05 01:00:14 +06",
                    "limit_currency_shortname": "USD",
                    "limit_expense_category": "PRODUCT",
                    "account_from": 1
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "limit_sum должен быть указан"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createMonthlyLimit_PayloadIsInvalid_LimitSumIsNegative_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/monthly-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "limit_sum": -1,
                    "limit_datetime": "2024-08-05 01:00:14 +06",
                    "limit_currency_shortname": "USD",
                    "limit_expense_category": "PRODUCT",
                    "account_from": 1
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "limit_sum меньше 0"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createMonthlyLimit_PayloadIsInvalid_DatetimeIsNull_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/monthly-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "limit_sum": 2000,
                    "limit_datetime": null,
                    "limit_currency_shortname": "USD",
                    "limit_expense_category": "PRODUCT",
                    "account_from": 1
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "datetime должен быть указан",
                                "datetime не должен быть пустым"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createMonthlyLimit_PayloadIsInvalid_DatetimeIsBlank_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/monthly-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "limit_sum": 2000,
                    "limit_datetime": "    ",
                    "limit_currency_shortname": "USD",
                    "limit_expense_category": "PRODUCT",
                    "account_from": 1
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "datetime не должен быть пустым"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createMonthlyLimit_PayloadIsInvalid_CurrencyShortnameIsNull_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/monthly-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "limit_sum": 2000,
                    "limit_datetime": "2024-08-05 01:00:14 +06",
                    "limit_currency_shortname": null,
                    "limit_expense_category": "PRODUCT",
                    "account_from": 1
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "currency_shortname должен быть указан",
                                "currency_shortname не должен быть пустым"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createMonthlyLimit_PayloadIsInvalid_CurrencyShortnameIsBlank_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/monthly-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "limit_sum": 2000,
                    "limit_datetime": "2024-08-05 01:00:14 +06",
                    "limit_currency_shortname": "     ",
                    "limit_expense_category": "PRODUCT",
                    "account_from": 1
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "currency_shortname не должен быть пустым"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createMonthlyLimit_PayloadIsInvalid_ExpenseCategoryIsNull_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/monthly-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "limit_sum": 2000,
                    "limit_datetime": "2024-08-05 01:00:14 +06",
                    "limit_currency_shortname": "USD",
                    "limit_expense_category": null,
                    "account_from": 1
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "expense_category должен быть указан",
                                "expense_category не должен быть пустым"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createMonthlyLimit_PayloadIsInvalid_ExpenseCategoryIsBlank_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/monthly-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "limit_sum": 2000,
                    "limit_datetime": "2024-08-05 01:00:14 +06",
                    "limit_currency_shortname": "USD",
                    "limit_expense_category": "       ",
                    "account_from": 1
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "expense_category не должен быть пустым"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createMonthlyLimit_PayloadIsInvalid_AccountFromIsNull_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/monthly-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "limit_sum": 2000,
                    "limit_datetime": "2024-08-05 01:00:14 +06",
                    "limit_currency_shortname": "USD",
                    "limit_expense_category": "PRODUCT",
                    "account_from": null
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "account_from должен быть указан"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createMonthlyLimit_PayloadIsInvalid_AccountFromIsLessThan1_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/monthly-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "limit_sum": 2000,
                    "limit_datetime": "2024-08-05 01:00:14 +06",
                    "limit_currency_shortname": "USD",
                    "limit_expense_category": "PRODUCT",
                    "account_from": 0
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "account_from меньше 1"
                            ]
                        }
                        """)
                );
    }
}