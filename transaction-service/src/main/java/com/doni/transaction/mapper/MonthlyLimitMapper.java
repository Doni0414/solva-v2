package com.doni.transaction.mapper;

import com.doni.transaction.dto.MonthlyLimitCreateDto;
import com.doni.transaction.dto.MonthlyLimitReadDto;
import com.doni.transaction.entity.ExpenseCategory;
import com.doni.transaction.entity.MonthlyLimit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MonthlyLimitMapper {
    @Mapping(source = "limitSum", target = "amount")
    @Mapping(source = "limitSum", target = "limitSum")
    @Mapping(source = "dateTime", target = "dateTime", qualifiedByName = "dateStringToZonedDate")
    @Mapping(source = "expenseCategory", target = "expenseCategory", qualifiedByName = "categoryStringToExpenseCategory")
    MonthlyLimit monthlyLimitCreateDtoToMonthlyLimit(MonthlyLimitCreateDto dto);
    MonthlyLimitReadDto monthlyLimitToMonthlyLimitReadDto(MonthlyLimit monthlyLimit);

    @Named("dateStringToZonedDate")
    static ZonedDateTime dateStringToZonedDate(String dateString) {
        String formatted = dateString.replaceFirst(" ", "T").replaceFirst(" ", "");
        return ZonedDateTime.parse(formatted);
    }

    @Named("categoryStringToExpenseCategory")
    static ExpenseCategory categoryStringToExpenseCategory(String categoryString) {
        return ExpenseCategory.valueOf(categoryString.toUpperCase());
    }

    List<MonthlyLimitReadDto> monthlyLimitsToMonthlyLimitReadDtos(List<MonthlyLimit> limits);
}
