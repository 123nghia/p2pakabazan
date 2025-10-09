package com.akabazan.service;

import com.akabazan.repository.entity.Wallet;
import com.akabazan.repository.constant.WalletTransactionType;

public interface WalletTransactionService {

    void record(Wallet wallet,
                WalletTransactionType type,
                double amount,
                double balanceBefore,
                double balanceAfter,
                double availableBefore,
                double availableAfter,
                Long performedBy,
                String referenceType,
                Long referenceId,
                String description);
}
