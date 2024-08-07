package com.doni.transaction.client;

import com.doni.transaction.config.FeignConfig;
import com.doni.transaction.dto.OpenExchangeRatesReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "currencyConversionFeignClient", url = "https://openexchangerates.org/api", configuration = FeignConfig.class)
public interface CurrencyConversionFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/latest.json?app_id={apiKey}")
    OpenExchangeRatesReadDto getCurrencyConversion(@PathVariable("apiKey") String apiKey);
}
