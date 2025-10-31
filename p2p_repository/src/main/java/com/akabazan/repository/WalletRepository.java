package com.akabazan.repository;

import com.akabazan.repository.entity.Wallet;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByUserIdAndToken(UUID userId, String token);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId AND w.token = :token")
    Optional<Wallet> lockByUserIdAndToken(UUID userId, String token);

    List<Wallet> findByUserId(UUID userId);
}
