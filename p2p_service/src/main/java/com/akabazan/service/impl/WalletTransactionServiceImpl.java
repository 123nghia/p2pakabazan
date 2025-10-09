package com.akabazan.service.impl;

import com.akabazan.repository.WalletTransactionRepository;
import com.akabazan.repository.constant.WalletTransactionType;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.repository.entity.WalletTransaction;
import com.akabazan.service.WalletTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;

    public WalletTransactionServiceImpl(WalletTransactionRepository walletTransactionRepository) {
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Override
    @Transactional
    public void record(Wallet wallet,
                       WalletTransactionType type,
                       double amount,
                       double balanceBefore,
                       double balanceAfter,
                       double availableBefore,
                       double availableAfter,
                       Long performedBy,
                       String referenceType,
                       Long referenceId,
                       String description) {
        if (wallet == null || wallet.getUser() == null) {
            return;
        }
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setUserId(wallet.getUser().getId());
        tx.setToken(wallet.getToken());
        tx.setType(type);
        tx.setAmount(amount);
        tx.setBalanceBefore(balanceBefore);
        tx.setBalanceAfter(balanceAfter);
        tx.setAvailableBefore(availableBefore);
        tx.setAvailableAfter(availableAfter);
        tx.setPerformedBy(performedBy);
        tx.setReferenceType(referenceType);
        tx.setReferenceId(referenceId);
        tx.setDescription(description);
        walletTransactionRepository.save(tx);
    }
}
