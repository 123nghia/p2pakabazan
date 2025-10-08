package com.akabazan.repository;

import com.akabazan.repository.entity.FiatAccount;
import com.akabazan.repository.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FiatAccountRepository extends JpaRepository<FiatAccount, Long> {
    Optional<FiatAccount> findByUserAndBankNameAndAccountNumberAndAccountHolder(
            User user,
            String bankName,
            String accountNumber,
            String accountHolder
    );

    List<FiatAccount> findByUserId(Long userId);
}
