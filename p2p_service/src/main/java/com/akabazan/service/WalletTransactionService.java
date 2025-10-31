package com.akabazan.service;

import com.akabazan.repository.entity.Wallet;
import com.akabazan.repository.constant.WalletTransactionType;
import java.util.UUID;

public interface WalletTransactionService {

    void record(Wallet wallet,
                WalletTransactionType type,
                double amount,
                double balanceBefore,
                double balanceAfter,
                double availableBefore,
                double availableAfter,
                UUID performedBy,
                String referenceType,
                UUID referenceId,
                String description);
}
