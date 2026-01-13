package com.akabazan.repository;

import com.akabazan.repository.entity.FiatAccount;
import com.akabazan.repository.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FiatAccountRepository extends JpaRepository<FiatAccount, UUID> {
        Optional<FiatAccount> findByUserAndBankNameAndAccountNumberAndAccountHolder(
                        User user,
                        String bankName,
                        String accountNumber,
                        String accountHolder);

        @Query("SELECT f FROM FiatAccount f WHERE f.user.id = :userId AND f.bankName = :bankName AND f.accountNumber = :accountNumber AND f.accountHolder = :accountHolder")
        Optional<FiatAccount> findByUserIdAndBankNameAndAccountNumberAndAccountHolder(
                        @Param("userId") UUID userId,
                        @Param("bankName") String bankName,
                        @Param("accountNumber") String accountNumber,
                        @Param("accountHolder") String accountHolder);

        List<FiatAccount> findByUserId(UUID userId);

        @Query("SELECT f FROM FiatAccount f WHERE f.user.id = :userId AND f.status = 'ACTIVE' AND f.deletedAt IS NULL")
        List<FiatAccount> findActiveByUserId(@Param("userId") UUID userId);

        @Query("SELECT f FROM FiatAccount f WHERE f.id = :id AND f.status = 'ACTIVE' AND f.deletedAt IS NULL")
        Optional<FiatAccount> findActiveById(@Param("id") UUID id);
}
