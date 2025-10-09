package com.akabazan.service.order.support;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.constant.WalletTransactionType;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.WalletTransactionService;
import org.springframework.stereotype.Component;

@Component
public class SellerFundsManager {

    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;

    public SellerFundsManager(WalletRepository walletRepository,
                              WalletTransactionService walletTransactionService) {
        this.walletRepository = walletRepository;
        this.walletTransactionService = walletTransactionService;
    }

    public void lock(User user, String token, double amount) {
        Wallet wallet = walletRepository.lockByUserIdAndToken(user.getId(), token)
                .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));

        if (wallet.getAvailableBalance() < amount) {
            throw new ApplicationException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        double balanceBefore = wallet.getBalance();
        double availableBefore = wallet.getAvailableBalance();
        wallet.setAvailableBalance(availableBefore - amount);
        walletRepository.save(wallet);
        walletTransactionService.record(
                wallet,
                WalletTransactionType.LOCK,
                amount,
                balanceBefore,
                wallet.getBalance(),
                availableBefore,
                wallet.getAvailableBalance(),
                user.getId(),
                "TRADE_LOCK",
                null,
                "Lock funds for P2P trade");
    }

    public void release(Long userId, String token, double amount) {
        Wallet wallet = walletRepository.lockByUserIdAndToken(userId, token)
                .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));
        double balanceBefore = wallet.getBalance();
        double availableBefore = wallet.getAvailableBalance();
        wallet.setAvailableBalance(availableBefore + amount);
        walletRepository.save(wallet);
        walletTransactionService.record(
                wallet,
                WalletTransactionType.UNLOCK,
                amount,
                balanceBefore,
                wallet.getBalance(),
                availableBefore,
                wallet.getAvailableBalance(),
                userId,
                "TRADE_UNLOCK",
                null,
                "Release locked funds");
    }
    public void releaseToBuyer(Trade trade) {
        User seller = trade.getSeller();
        User buyer = trade.getBuyer();
        String token = trade.getOrder().getToken();
        double amount = trade.getAmount();

        Wallet sellerWallet = getWalletOrThrow(seller, token);
        Wallet buyerWallet = getWalletOrThrow(buyer, token);

        double sellerBalanceBefore = sellerWallet.getBalance();
        double sellerAvailableBefore = sellerWallet.getAvailableBalance();
        sellerWallet.setBalance(sellerBalanceBefore - amount);

        double buyerBalanceBefore = buyerWallet.getBalance();
        double buyerAvailableBefore = buyerWallet.getAvailableBalance();
        buyerWallet.setAvailableBalance(buyerAvailableBefore + amount);
        buyerWallet.setBalance(buyerBalanceBefore + amount);

        walletRepository.save(sellerWallet);
        walletRepository.save(buyerWallet);

        walletTransactionService.record(
                sellerWallet,
                WalletTransactionType.DEBIT,
                amount,
                sellerBalanceBefore,
                sellerWallet.getBalance(),
                sellerAvailableBefore,
                sellerWallet.getAvailableBalance(),
                seller.getId(),
                "TRADE_SETTLEMENT",
                trade.getId(),
                "Release locked funds to buyer");

        walletTransactionService.record(
                buyerWallet,
                WalletTransactionType.CREDIT,
                amount,
                buyerBalanceBefore,
                buyerWallet.getBalance(),
                buyerAvailableBefore,
                buyerWallet.getAvailableBalance(),
                buyer.getId(),
                "TRADE_SETTLEMENT",
                trade.getId(),
                "Receive tokens from trade settlement");
    }

    public void refundToSeller(Trade trade) {
        User seller = trade.getSeller();
        String token = trade.getOrder().getToken();
        double amount = trade.getAmount();

        Wallet sellerWallet = getWalletOrThrow(seller, token);
        double balanceBefore = sellerWallet.getBalance();
        double availableBefore = sellerWallet.getAvailableBalance();
        sellerWallet.setAvailableBalance(availableBefore + amount);

        walletRepository.save(sellerWallet);

        walletTransactionService.record(
                sellerWallet,
                WalletTransactionType.UNLOCK,
                amount,
                balanceBefore,
                sellerWallet.getBalance(),
                availableBefore,
                sellerWallet.getAvailableBalance(),
                seller.getId(),
                "TRADE_REFUND",
                trade.getId(),
                "Refund locked funds to seller");
    }

    private Wallet getWalletOrThrow(User user, String token) {
        return walletRepository.lockByUserIdAndToken(user.getId(), token)
                .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));
    }
}
