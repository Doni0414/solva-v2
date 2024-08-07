package com.doni.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record OpenExchangeRatesReadDto(
        @JsonProperty("base")
        String base,

        @JsonProperty("rates")
        Map<String, Double> rates) {
}
