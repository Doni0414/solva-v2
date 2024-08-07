package com.doni.transaction.service;

import com.doni.transaction.dto.MonthlyLimitCreateDto;
import com.doni.transaction.entity.MonthlyLimit;
import com.doni.transaction.mapper.MonthlyLimitMapper;
import com.doni.transaction.repository.MonthlyLimitRepository;
import com.doni.transaction.utils.CurrencyConversionUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DefaultMonthlyLimitService implements MonthlyLimitService {
    private final MonthlyLimitMapper monthlyLimitMapper;
    private final MonthlyLimitRepository monthlyLimitRepository;
    private final CurrencyConversionUtility currencyConversionUtility;

    @Override
    @Transactional
    public MonthlyLimit createMonthlyLimit(MonthlyLimitCreateDto dto) {
        MonthlyLimit monthlyLimit = monthlyLimitMapper.monthlyLimitCreateDtoToMonthlyLimit(dto);
        if (monthlyLimit.getLimitSum() == null) {
            monthlyLimit.setLimitSum(1000D);
        }
        monthlyLimit.setAmount(monthlyLimit.getLimitSum());

        monthlyLimitRepository.findByExpenseCategoryAndAccountFromOrderByDateTime(monthlyLimit.getExpenseCategory(), monthlyLimit.getAccountFrom(), monthlyLimit.getDateTime())
                .ifPresent(found -> {
                    double previousLimitSum = currencyConversionUtility.convertToUSD(found.getCurrencyShortname(), found.getLimitSum());
                    double previousAmount = currencyConversionUtility.convertToUSD(found.getCurrencyShortname(), found.getAmount());
                    double limitSum = currencyConversionUtility.convertToUSD(monthlyLimit.getCurrencyShortname(), monthlyLimit.getLimitSum());

                    double updatedLimitSum = currencyConversionUtility.convertFromUSD(monthlyLimit.getCurrencyShortname(),
                            limitSum - previousLimitSum + previousAmount);

                    monthlyLimit.setAmount(updatedLimitSum);
                });
        return monthlyLimitRepository.save(monthlyLimit);
    }

    @Override
    public List<MonthlyLimit> findMonthlyLimits() {
        return monthlyLimitRepository.findAll();
    }
}
