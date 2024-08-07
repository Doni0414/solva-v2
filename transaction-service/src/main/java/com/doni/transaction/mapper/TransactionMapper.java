package com.doni.transaction.mapper;

import com.doni.transaction.dto.TransactionCreateDto;
import com.doni.transaction.dto.TransactionReadDto;
import com.doni.transaction.entity.ExpenseCategory;
import com.doni.transaction.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "monthlyLimit.limitSum", target = "limitSum")
    @Mapping(source = "monthlyLimit.currencyShortname", target = "limitCurrencyShortname")
    @Mapping(source = "monthlyLimit.dateTime", target = "limitDateTime")
    TransactionReadDto transactionToTransactionReadDto(Transaction transaction);

    @Mapping(source = "dateTime", target = "dateTime", qualifiedByName = "dateStringToZonedDate")
    @Mapping(source = "expenseCategory", target = "expenseCategory", qualifiedByName = "categoryStringToExpenseCategory")
    Transaction transactionCreateDtoToTransaction(TransactionCreateDto dto);

    List<TransactionReadDto> transactionsToTransactionReadDtos(List<Transaction> transactions);

    @Named("dateStringToZonedDate")
    static ZonedDateTime dateStringToZonedDate(String dateString) {
        String formatted = dateString.replaceFirst(" ", "T").replaceFirst(" ", "");
        return ZonedDateTime.parse(formatted);
    }

    @Named("categoryStringToExpenseCategory")
    static ExpenseCategory categoryStringToExpenseCategory(String categoryString) {
        return ExpenseCategory.valueOf(categoryString.toUpperCase());
    }
}
