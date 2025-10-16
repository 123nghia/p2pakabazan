package com.akabazan.service.util;

import com.akabazan.repository.constant.WalletTransactionType;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.WalletTransactionService;
import org.springframework.stereotype.Component;

/**
 * Utility class for wallet transaction operations
 * Reduces code duplication across service classes
 */
@Component
public class WalletTransactionHelper {

    private final WalletTransactionService walletTransactionService;

    public WalletTransactionHelper(WalletTransactionService walletTransactionService) {
        this.walletTransactionService = walletTransactionService;
    }

    /**
     * Records a wallet transaction with all necessary details
     */
    public void recordTransaction(Wallet wallet, WalletTransactionType type, double amount,
                                 Long userId, String reference, String description) {
        double balanceBefore = wallet.getBalance();
        double availableBefore = wallet.getAvailableBalance();
        
        walletTransactionService.record(
                wallet,
                type,
                amount,
                balanceBefore,
                wallet.getBalance(),
                availableBefore,
                wallet.getAvailableBalance(),
                userId,
                reference,
                null,
                description
        );
    }

    /**
     * Records a wallet transaction with trade reference
     */
    public void recordTradeTransaction(Wallet wallet, WalletTransactionType type, double amount,
                                      Long userId, Long tradeId, String reference, String description) {
        double balanceBefore = wallet.getBalance();
        double availableBefore = wallet.getAvailableBalance();
        
        walletTransactionService.record(
                wallet,
                type,
                amount,
                balanceBefore,
                wallet.getBalance(),
                availableBefore,
                wallet.getAvailableBalance(),
                userId,
                reference,
                tradeId,
                description
        );
    }
}
