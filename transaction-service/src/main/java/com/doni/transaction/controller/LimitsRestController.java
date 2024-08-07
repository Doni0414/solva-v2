package com.doni.transaction.controller;

import com.doni.transaction.dto.MonthlyLimitCreateDto;
import com.doni.transaction.dto.MonthlyLimitReadDto;
import com.doni.transaction.entity.MonthlyLimit;
import com.doni.transaction.mapper.MonthlyLimitMapper;
import com.doni.transaction.service.MonthlyLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
@Tag(name = "LimitsRestController", description = "Эндпоинты для работы с месячными лимитами")
@RequestMapping("/transaction-api/monthly-limits")
public class LimitsRestController {
    private final MonthlyLimitMapper monthlyLimitMapper;
    private final MonthlyLimitService monthlyLimitService;

    @Operation(summary = "get", description = "получение всех лимитов")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Успешное получение лимитов",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MonthlyLimitReadDto.class)))))
    @GetMapping
    public List<MonthlyLimitReadDto> getAllLimits() {
        List<MonthlyLimit> limits = monthlyLimitService.findMonthlyLimits();
        return monthlyLimitMapper.monthlyLimitsToMonthlyLimitReadDtos(limits);
    }

    @Operation(summary = "Создание лимита", description = "Эндпоинт создания нового месячного лимита",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = MonthlyLimitCreateDto.class),
    examples = @ExampleObject(value = "{\"limit_sum\": 1000, \"limit_datetime\": \"2024-08-08 16:40:00 +06\", \"limit_currency_shortname\": \"USD\", \"limit_expense_category\": \"SERVICE\", \"account_from\": 1}"))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешное создания месячного лимита",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MonthlyLimitReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Плохой запрос",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<MonthlyLimitReadDto> createMonthlyLimit(@RequestBody @Valid MonthlyLimitCreateDto dto,
                                                                  BindingResult bindingResult,
                                                                  UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException ex) {
                throw ex;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            MonthlyLimit monthlyLimit = monthlyLimitService.createMonthlyLimit(dto);
            return ResponseEntity.created(uriComponentsBuilder.replacePath("/transaction-api/monthly-limits/{id}")
                    .build(Map.of("id", monthlyLimit.getId())))
                    .body(monthlyLimitMapper.monthlyLimitToMonthlyLimitReadDto(monthlyLimit));
        }
    }
}
