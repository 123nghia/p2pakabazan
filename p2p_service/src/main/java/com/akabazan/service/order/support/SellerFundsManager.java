package com.akabazan.service.order.support;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import org.springframework.stereotype.Component;

@Component
public class SellerFundsManager {

    private final WalletRepository walletRepository;

    public SellerFundsManager(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void lock(User user, String token, double amount) {
        Wallet wallet = walletRepository.lockByUserIdAndToken(user.getId(), token)
                .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));

        if (wallet.getAvailableBalance() < amount) {
            throw new ApplicationException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance() - amount);
        walletRepository.save(wallet);
    }

    public void release(Long userId, String token, double amount) {
        Wallet wallet = walletRepository.lockByUserIdAndToken(userId, token)
                .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));
        wallet.setAvailableBalance(wallet.getAvailableBalance() + amount);
        walletRepository.save(wallet);
    }
}
