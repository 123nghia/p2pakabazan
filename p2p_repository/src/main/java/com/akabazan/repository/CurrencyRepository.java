package com.akabazan.repository;

import com.akabazan.repository.constant.CurrencyType;
import com.akabazan.repository.entity.Currency;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    List<Currency> findAllByTypeAndActiveTrueOrderByDisplayOrderAscCodeAsc(CurrencyType type);

    List<Currency> findAllByActiveTrueOrderByDisplayOrderAscCodeAsc();
}
