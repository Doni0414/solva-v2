package com.doni.transaction.service;

import com.doni.transaction.dto.MonthlyLimitCreateDto;
import com.doni.transaction.entity.MonthlyLimit;

import java.util.List;

public interface MonthlyLimitService {
    MonthlyLimit createMonthlyLimit(MonthlyLimitCreateDto dto);

    List<MonthlyLimit> findMonthlyLimits();
}
