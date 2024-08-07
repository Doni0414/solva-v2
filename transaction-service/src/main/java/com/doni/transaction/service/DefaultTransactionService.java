package com.doni.transaction.service;

import com.doni.transaction.dto.TransactionCreateDto;
import com.doni.transaction.dto.TransactionReadDto;
import com.doni.transaction.entity.Transaction;
import com.doni.transaction.mapper.MonthlyLimitMapper;
import com.doni.transaction.mapper.TransactionMapper;
import com.doni.transaction.repository.MonthlyLimitRepository;
import com.doni.transaction.repository.TransactionRepository;
import com.doni.transaction.utils.CurrencyConversionUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultTransactionService implements TransactionService {
    private final TransactionMapper transactionMapper;
    private final MonthlyLimitMapper monthlyLimitMapper;
    private final TransactionRepository transactionRepository;
    private final MonthlyLimitRepository monthlyLimitRepository;
    private final CurrencyConversionUtility currencyConversionUtility;

    @Override
    @Transactional
    public Transaction createTransaction(TransactionCreateDto dto) {
        Transaction transaction = transactionMapper.transactionCreateDtoToTransaction(dto);
        transaction.setLimitExceeded(false);

        monthlyLimitRepository.findByExpenseCategoryAndAccountFromOrderByDateTime(transaction.getExpenseCategory(), dto.getAccountFrom(), transaction.getDateTime())
                .ifPresent(monthlyLimit -> {
                    double limitAmount = currencyConversionUtility.convertToUSD(monthlyLimit.getCurrencyShortname(), monthlyLimit.getAmount());
                    double transactionAmount = currencyConversionUtility.convertToUSD(transaction.getCurrencyShortname(), transaction.getSum());

                    double updatedLimitAmount = limitAmount - transactionAmount;

                    double convertedFromUSD = currencyConversionUtility
                            .convertFromUSD(monthlyLimit.getCurrencyShortname(), updatedLimitAmount);
                    monthlyLimit.setAmount(convertedFromUSD);

                    monthlyLimit.addTransaction(transaction);

                    if (convertedFromUSD < 0) {
                        transaction.setLimitExceeded(true);
                    }
                });
        return transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionReadDto> findTransactionsWithLimitExceeded() {
        List<Transaction> transactions = transactionRepository.findAllByLimitExceeded(true);
        return transactionMapper.transactionsToTransactionReadDtos(transactions);
    }
}
