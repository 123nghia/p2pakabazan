package com.akabazan.service.order.support;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.entity.Trade;
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
     
        public  void releaseToBuyer(Trade trade) {

        User seller = trade.getSeller();
        User buyer = trade.getBuyer();
        String token =  trade.getOrder().getToken();
        double amount = trade.getAmount();

        Wallet sellerWallet = getWalletOrThrow(seller, token);
        Wallet buyerWallet = getWalletOrThrow(buyer, token);


        // Trừ locked balance của seller
        // sellerWallet.setBalance(sellerWallet.getBalance() - amount);
        sellerWallet.setBalance(sellerWallet.getBalance() - amount);

        // Cộng available và tổng balance cho buyer
        buyerWallet.setAvailableBalance(buyerWallet.getAvailableBalance() + amount);
        buyerWallet.setBalance(buyerWallet.getBalance() + amount);

        walletRepository.save(sellerWallet);
        walletRepository.save(buyerWallet);

        }

        public  void refundToSeller(Trade trade) {

                User seller = trade.getSeller();
                String token = trade.getOrder().getToken();
                double amount = trade.getAmount();

                Wallet sellerWallet = getWalletOrThrow(seller, token);
                
                sellerWallet.setAvailableBalance(sellerWallet.getAvailableBalance() + amount);

                walletRepository.save(sellerWallet);

        }


        private Wallet getWalletOrThrow(User user, String token) {
        return walletRepository.findByUserIdAndToken(user.getId(), token)
        .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));
        }

      
    }
