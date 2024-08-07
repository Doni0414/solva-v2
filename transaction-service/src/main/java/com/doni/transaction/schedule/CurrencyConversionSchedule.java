package com.doni.transaction.schedule;

import com.doni.transaction.service.CurrencyService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyConversionSchedule {
    private final CurrencyService currencyService;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateDailyCurrencyConversion() {
        currencyService.updateCurrencyConversion();
    }

    @PostConstruct
    public void onStartUp() {
        System.out.println("CurrencyConversionSchedule: post construct");
        updateDailyCurrencyConversion();
    }
}
