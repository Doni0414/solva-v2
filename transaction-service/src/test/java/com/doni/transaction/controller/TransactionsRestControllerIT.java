package com.doni.transaction.controller;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
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
class TransactionsRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager entityManager;

    @Test
    @Sql(scripts = {"/sql/monthly_limits.sql", "/sql/transactions.sql"})
    void getTransactionsWithLimitExceeded_ReturnsOk() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get("/transaction-api/transactions/limit-exceeded");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                        [
                            {
                                "account_from": 123, 
                                "account_to": 1001, 
                                "currency_shortname": "USD", 
                                "sum": 100, 
                                "expense_category": "SERVICE", 
                                "datetime": "2003-04-12T04:05:06+06:00", 
                                "limit_exceeded": true, 
                                "limit_sum": 1000,
                                "limit_datetime": "2003-04-12T04:05:06+06:00",
                                "limit_currency_shortname": "USD"
                            },
                            {
                                "account_from": 123, 
                                "account_to": 1002, 
                                "currency_shortname": "USD", 
                                "sum": 100, 
                                "expense_category": "PRODUCT", 
                                "datetime": "2003-04-12T04:05:06+06:00", 
                                "limit_exceeded": true, 
                                "limit_sum": 1000,
                                "limit_datetime": "2024-04-12T04:05:06+06:00",
                                "limit_currency_shortname": "USD"
                            }
                        ]
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsValid_ReturnsCreated() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 9999999999,
                    "currency_shortname": "KZT",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost/transaction-api/transactions/1"),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                        {
                            "account_from": 123,
                            "account_to": 9999999999,
                            "currency_shortname": "KZT",
                            "sum": 10000.45,
                            "expense_category": "SERVICE",
                            "datetime": "2022-01-30T00:00:00+06:00",
                            "limit_exceeded": false
                        }
                        """)
                );
    }


    @Test
    void createTransaction_PayloadIsInvalid_AccountFromIsNull_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": null,
                    "account_to": 9999999999,
                    "currency_shortname": "KZT",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
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
    void createTransaction_PayloadIsInvalid_AccountFromIsLessThanMin_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 0,
                    "account_to": 9999999999,
                    "currency_shortname": "KZT",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "account_from не должен быть меньше 1"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_AccountFromIsGreaterThanMax_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 10000000000,
                    "account_to": 9999999999,
                    "currency_shortname": "KZT",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "account_from не должен быть больше 9999999999"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_AccountToIsNull_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": null,
                    "currency_shortname": "KZT",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "account_to должен быть указан"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_AccountToIsLessThanMin_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 0,
                    "currency_shortname": "KZT",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "account_to не должен быть меньше 1"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_AccountToIsGreaterThanMax_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 19999999999,
                    "currency_shortname": "KZT",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "account_to не должен быть больше 9999999999"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_CurrencyShortnameIsNull_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 9999999999,
                    "currency_shortname": null,
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "currency_shortname не должен быть пустым",
                                "currency_shortname должен быть указан"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_CurrencyShortnameIsBlank_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 9999999999,
                    "currency_shortname": "   ",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
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
    void createTransaction_PayloadIsInvalid_CurrencyShortnameLengthIsLessThanMin_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 9999999999,
                    "currency_shortname": "KZ",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "Длина currency_shortname должно быть между 3 и 3 символами"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_CurrencyShortnameLengthIsGreaterThanMax_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 9999999999,
                    "currency_shortname": "KZTZ",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "Длина currency_shortname должно быть между 3 и 3 символами"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_SumIsNull_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 9999999999,
                    "currency_shortname": "KZT",
                    "sum": null,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "sum должен быть указан"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_SumIsNegative_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 9999999999,
                    "currency_shortname": "KZT",
                    "sum": -1,
                    "expense_category": "service",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "sum должен быть меньше 0"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_ExpenseCategoryIsNull_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 9999999999,
                    "currency_shortname": "KZT",
                    "sum": 10000.45,
                    "expense_category": null,
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "expense_category не должен быть пустым",
                                "expense_category должен быть указан"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_ExpenseCategoryIsBlank_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 9999999999,
                    "currency_shortname": "KZT",
                    "sum": 10000.45,
                    "expense_category": "       ",
                    "datetime": "2022-01-30 00:00:00 +06"
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
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
    void createTransaction_PayloadIsInvalid_DatetimeIsNull_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 9999999999,
                    "currency_shortname": "KZT",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": null
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                            "detail": "Плохой запрос",
                            "errors": [
                                "datetime не должен быть пустым",
                                "datetime должен быть указан"
                            ]
                        }
                        """)
                );
    }

    @Test
    void createTransaction_PayloadIsInvalid_DatetimeIsBlank_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/transaction-api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "account_from": 123,
                    "account_to": 9999999999,
                    "currency_shortname": "KZT",
                    "sum": 10000.45,
                    "expense_category": "service",
                    "datetime": "     "
                }
                """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
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
}