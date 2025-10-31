package com.akabazan.repository;

import com.akabazan.repository.entity.FiatAccount;
import com.akabazan.repository.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FiatAccountRepository extends JpaRepository<FiatAccount, UUID> {
    Optional<FiatAccount> findByUserAndBankNameAndAccountNumberAndAccountHolder(
            User user,
            String bankName,
            String accountNumber,
            String accountHolder
    );

    List<FiatAccount> findByUserId(UUID userId);
}
