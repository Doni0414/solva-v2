package com.doni.transaction.repository;

import com.doni.transaction.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

    Optional<Currency> findByCurrencyShortname(String currencyShortname);
}
