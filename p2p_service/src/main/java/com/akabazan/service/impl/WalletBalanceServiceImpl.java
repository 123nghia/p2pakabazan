package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.WalletBalanceService;
import com.akabazan.service.dto.WalletBalanceResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletBalanceServiceImpl implements WalletBalanceService {

    private final CurrentUserService currentUserService;
    private final WalletRepository walletRepository;

    public WalletBalanceServiceImpl(CurrentUserService currentUserService,
                                    WalletRepository walletRepository) {
        this.currentUserService = currentUserService;
        this.walletRepository = walletRepository;
    }

    @Override
    public List<WalletBalanceResult> getCurrentUserBalances() {
        Long userId = currentUserService.getCurrentUserId()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        return walletRepository.findByUserId(userId).stream()
                .map(this::toResult)
                .collect(Collectors.toList());
    }

    private WalletBalanceResult toResult(Wallet wallet) {
        WalletBalanceResult result = new WalletBalanceResult();
        result.setToken(wallet.getToken());
        result.setBalance(wallet.getBalance());
        result.setAvailableBalance(wallet.getAvailableBalance());
        return result;
    }
}
