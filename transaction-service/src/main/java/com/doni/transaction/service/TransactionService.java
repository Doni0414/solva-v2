package com.doni.transaction.service;

import com.doni.transaction.dto.TransactionCreateDto;
import com.doni.transaction.dto.TransactionReadDto;
import com.doni.transaction.entity.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(TransactionCreateDto dto);

    List<TransactionReadDto> findTransactionsWithLimitExceeded();
}
