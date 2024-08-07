package com.doni.transaction.controller;

import com.doni.transaction.dto.TransactionCreateDto;
import com.doni.transaction.dto.TransactionReadDto;
import com.doni.transaction.entity.Transaction;
import com.doni.transaction.mapper.TransactionMapper;
import com.doni.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction-api/transactions")
public class TransactionsRestController {
    private final TransactionMapper transactionMapper;
    private final TransactionService transactionService;

    @Operation(summary = "Транзакции с превышением лимита", description = "Получение транзакции с превышением лимита")
    @ApiResponse(responseCode = "200", description = "Успешное получение транзакции, которые превысили лимит",
    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TransactionReadDto.class))))
    @GetMapping("/limit-exceeded")
    public List<TransactionReadDto> getTransactionsWithLimitExceeded() {
        return transactionService.findTransactionsWithLimitExceeded();
    }

    @Operation(summary = "Создание транзакций", description = "Создает транзакцию и дополнительно проверяет превысил ли лимит данная транзакция",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = TransactionCreateDto.class),
    examples = @ExampleObject(value = "{\"account_from\": 1, \"account_to\": 5, \"currency_shortname\": \"USD\", \"sum\": 200, \"expense_category\": \"SERVICE\", \"datetime\": \"2024-09-02 16:52:00 +06\"}"))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешное создание транзакций",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Плохой запрос",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<TransactionReadDto> createTransaction(@Valid @RequestBody TransactionCreateDto dto,
                                                                BindingResult bindingResult,
                                                                UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException ex) {
                throw ex;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Transaction transaction = transactionService.createTransaction(dto);
            System.out.println(transaction);
            return ResponseEntity.created(uriComponentsBuilder.replacePath("/transaction-api/transactions/{id}")
                    .build(Map.of("id", transaction.getId())))
                    .body(transactionMapper.transactionToTransactionReadDto(transaction));
        }
    }

}
