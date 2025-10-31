package com.akabazan.repository;

import com.akabazan.repository.entity.WalletTransaction;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {
}
