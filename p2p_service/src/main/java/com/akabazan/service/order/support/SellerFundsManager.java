package com.akabazan.service.order.support;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.constant.WalletTransactionType;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.WalletTransactionService;
import com.akabazan.service.partner.PartnerFundsApiClient;
import com.akabazan.service.partner.dto.LockFundsRequest;
import com.akabazan.service.partner.dto.LockFundsResponse;
import com.akabazan.service.partner.dto.TransferFundsRequest;
import com.akabazan.service.partner.dto.UnlockFundsRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SellerFundsManager {

    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;
    private final PartnerFundsApiClient partnerFundsApiClient;

    public SellerFundsManager(WalletRepository walletRepository,
                              WalletTransactionService walletTransactionService,
                              PartnerFundsApiClient partnerFundsApiClient) {
        this.walletRepository = walletRepository;
        this.walletTransactionService = walletTransactionService;
        this.partnerFundsApiClient = partnerFundsApiClient;
    }

    public void lockForSellOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("order must not be null");
        }
        User seller = order.getUser();
        if (seller == null) {
            throw new IllegalArgumentException("order.user must not be null");
        }

        String token = order.getToken();
        double amount = order.getAmount() == null ? 0.0 : order.getAmount();
        if (amount <= 0) {
            throw new IllegalArgumentException("order.amount must be > 0");
        }

        if (!isPartnerUser(seller)) {
            lockInternal(seller, token, amount);
            return;
        }

        String partnerId = requirePartnerId(seller);
        String externalUserId = requireExternalUserId(seller);

        String requestId = "ORDER_LOCK:" + order.getId();
        LockFundsResponse response = partnerFundsApiClient.lock(partnerId, new LockFundsRequest(
                requestId,
                externalUserId,
                token,
                formatAmount(amount),
                "ORDER",
                order.getId() == null ? null : order.getId().toString()
        ));

        if (response == null || response.lockId() == null || response.lockId().isBlank()) {
            throw new IllegalStateException("Partner lock failed: missing lockId");
        }

        order.setFundsLockId(response.lockId());
    }

    public void lockForBuyTrade(Trade trade) {
        if (trade == null) {
            throw new IllegalArgumentException("trade must not be null");
        }
        if (trade.getSeller() == null) {
            throw new IllegalArgumentException("trade.seller must not be null");
        }
        if (trade.getOrder() == null) {
            throw new IllegalArgumentException("trade.order must not be null");
        }

        User seller = trade.getSeller();
        String token = trade.getOrder().getToken();
        double amount = trade.getAmount();

        if (!isPartnerUser(seller)) {
            lockInternal(seller, token, amount);
            return;
        }

        String partnerId = requirePartnerId(seller);
        String externalUserId = requireExternalUserId(seller);

        String requestId = "TRADE_LOCK:" + trade.getId();
        LockFundsResponse response = partnerFundsApiClient.lock(partnerId, new LockFundsRequest(
                requestId,
                externalUserId,
                token,
                formatAmount(amount),
                "TRADE",
                trade.getId() == null ? null : trade.getId().toString()
        ));

        if (response == null || response.lockId() == null || response.lockId().isBlank()) {
            throw new IllegalStateException("Partner lock failed: missing lockId");
        }

        trade.setFundsLockId(response.lockId());
    }

    public void unlockSellOrderRemainder(Order order, double amount) {
        if (order == null) {
            throw new IllegalArgumentException("order must not be null");
        }
        User seller = order.getUser();
        if (seller == null) {
            throw new IllegalArgumentException("order.user must not be null");
        }
        if (amount <= 0) {
            return;
        }

        if (!isPartnerUser(seller)) {
            unlockInternal(seller.getId(), order.getToken(), amount);
            return;
        }

        String partnerId = requirePartnerId(seller);
        String lockId = requireTrimmed(order.getFundsLockId(), "order.fundsLockId");

        String requestId = "ORDER_UNLOCK:" + order.getId() + ":" + formatAmount(amount);
        partnerFundsApiClient.unlock(partnerId, new UnlockFundsRequest(
                requestId,
                lockId,
                formatAmount(amount)
        ));
    }

    public void unlockBuyTrade(Trade trade) {
        if (trade == null) {
            throw new IllegalArgumentException("trade must not be null");
        }
        if (trade.getSeller() == null) {
            throw new IllegalArgumentException("trade.seller must not be null");
        }
        if (trade.getOrder() == null) {
            throw new IllegalArgumentException("trade.order must not be null");
        }

        User seller = trade.getSeller();
        String token = trade.getOrder().getToken();
        double amount = trade.getAmount();

        if (!isPartnerUser(seller)) {
            unlockInternal(seller.getId(), token, amount);
            return;
        }

        String partnerId = requirePartnerId(seller);
        String lockId = requireTrimmed(trade.getFundsLockId(), "trade.fundsLockId");

        String requestId = "TRADE_UNLOCK:" + trade.getId();
        partnerFundsApiClient.unlock(partnerId, new UnlockFundsRequest(
                requestId,
                lockId,
                null
        ));
    }

    public void settleTrade(Trade trade) {
        if (trade == null) {
            throw new IllegalArgumentException("trade must not be null");
        }
        if (trade.getOrder() == null) {
            throw new IllegalArgumentException("trade.order must not be null");
        }
        if (trade.getSeller() == null || trade.getBuyer() == null) {
            throw new IllegalArgumentException("trade buyer/seller must not be null");
        }

        Order order = trade.getOrder();
        User seller = trade.getSeller();
        User buyer = trade.getBuyer();
        String token = order.getToken();
        double amount = trade.getAmount();

        boolean sellerPartner = isPartnerUser(seller);
        boolean buyerPartner = isPartnerUser(buyer);

        if (!sellerPartner && !buyerPartner) {
            settleInternal(trade);
            return;
        }

        if (!sellerPartner || !buyerPartner) {
            throw new IllegalStateException("Cross-system settlement is not supported (sellerPartner=" + sellerPartner + ", buyerPartner=" + buyerPartner + ")");
        }

        String partnerId = requirePartnerId(seller);
        if (!partnerId.equals(requirePartnerId(buyer))) {
            throw new IllegalStateException("Cross-partner trade is not supported");
        }

        String sellerExternalId = requireExternalUserId(seller);
        String buyerExternalId = requireExternalUserId(buyer);

        String lockId;
        if ("SELL".equalsIgnoreCase(order.getType())) {
            lockId = requireTrimmed(order.getFundsLockId(), "order.fundsLockId");
        } else {
            lockId = requireTrimmed(trade.getFundsLockId(), "trade.fundsLockId");
        }

        String requestId = "TRADE_TRANSFER:" + trade.getId();
        partnerFundsApiClient.transfer(partnerId, new TransferFundsRequest(
                requestId,
                lockId,
                sellerExternalId,
                buyerExternalId,
                token,
                formatAmount(amount),
                "TRADE",
                trade.getId() == null ? null : trade.getId().toString()
        ));
    }

    public void releaseToBuyer(Trade trade) {
        settleTrade(trade);
    }

    private void lockInternal(User user, String token, double amount) {
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

    private void unlockInternal(UUID userId, String token, double amount) {
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

    private void settleInternal(Trade trade) {
        User seller = trade.getSeller();
        User buyer = trade.getBuyer();
        String token = trade.getOrder().getToken();
        double amount = trade.getAmount();

        Wallet buyerWallet = walletRepository.findByUserIdAndToken(buyer.getId(), token)
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setUser(buyer);
                    w.setToken(token);
                    w.setAddress("generated-address-" + buyer.getId());
                    w.setBalance(0.0);
                    w.setAvailableBalance(0.0);
                    return walletRepository.save(w);
                });

        Wallet sellerWallet = getWalletOrThrow(seller, token);

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

        if (isPartnerUser(seller)) {
            if ("SELL".equalsIgnoreCase(trade.getOrder().getType())) {
                unlockSellOrderRemainder(trade.getOrder(), amount);
            } else {
                unlockBuyTrade(trade);
            }
            return;
        }

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

    private static boolean isPartnerUser(User user) {
        if (user == null) {
            return false;
        }
        return isNotBlank(user.getType()) || isNotBlank(user.getRelId());
    }

    private static String requirePartnerId(User user) {
        return requireTrimmed(user.getType(), "user.type");
    }

    private static String requireExternalUserId(User user) {
        return requireTrimmed(user.getRelId(), "user.relId");
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static String requireTrimmed(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(field + " must not be blank");
        }
        return value.trim();
    }

    private static String formatAmount(double amount) {
        return java.math.BigDecimal.valueOf(amount).stripTrailingZeros().toPlainString();
    }
}
