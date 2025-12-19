package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.WalletBalanceService;
import com.akabazan.service.dto.WalletBalanceResult;
import com.akabazan.service.partner.PartnerFundsApiClient;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class WalletBalanceServiceImpl implements WalletBalanceService {

    private final CurrentUserService currentUserService;
    private final WalletRepository walletRepository;
    private final PartnerFundsApiClient partnerFundsApiClient;

    public WalletBalanceServiceImpl(CurrentUserService currentUserService,
                                    WalletRepository walletRepository,
                                    PartnerFundsApiClient partnerFundsApiClient) {
        this.currentUserService = currentUserService;
        this.walletRepository = walletRepository;
        this.partnerFundsApiClient = partnerFundsApiClient;
    }

    @Override
    public List<WalletBalanceResult> getCurrentUserBalances() {
        var user = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (isPartnerUser(user)) {
            var response = partnerFundsApiClient.getBalances(user.getType(), user.getRelId(), null);
            if (response == null || response.balances() == null) {
                return List.of();
            }
            return response.balances().stream()
                    .map(b -> toPartnerResult(b.asset(), b.available(), b.locked()))
                    .toList();
        }

        UUID userId = user.getId();
        return walletRepository.findByUserId(userId).stream().map(this::toResult).toList();
    }

    private WalletBalanceResult toResult(Wallet wallet) {
        WalletBalanceResult result = new WalletBalanceResult();
        result.setToken(wallet.getToken());
        result.setBalance(wallet.getBalance());
        result.setAvailableBalance(wallet.getAvailableBalance());
        return result;
    }

    private static boolean isPartnerUser(com.akabazan.repository.entity.User user) {
        if (user == null) {
            return false;
        }
        return (user.getType() != null && !user.getType().isBlank())
                || (user.getRelId() != null && !user.getRelId().isBlank());
    }

    private static WalletBalanceResult toPartnerResult(String asset, String availableRaw, String lockedRaw) {
        java.math.BigDecimal available = parseDecimalOrZero(availableRaw);
        java.math.BigDecimal locked = parseDecimalOrZero(lockedRaw);

        WalletBalanceResult result = new WalletBalanceResult();
        result.setToken(asset);
        result.setAvailableBalance(available.doubleValue());
        result.setBalance(available.add(locked).doubleValue());
        return result;
    }

    private static java.math.BigDecimal parseDecimalOrZero(String value) {
        if (value == null || value.isBlank()) {
            return java.math.BigDecimal.ZERO;
        }
        try {
            return new java.math.BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return java.math.BigDecimal.ZERO;
        }
    }
}
