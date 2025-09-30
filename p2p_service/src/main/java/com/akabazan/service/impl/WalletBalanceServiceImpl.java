package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.WalletBalanceService;
import com.akabazan.service.dto.WalletBalanceDTO;
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
    public List<WalletBalanceDTO> getCurrentUserBalances() {
        Long userId = currentUserService.getCurrentUserId()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        return walletRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private WalletBalanceDTO toDto(Wallet wallet) {
        WalletBalanceDTO dto = new WalletBalanceDTO();
        dto.setToken(wallet.getToken());
        dto.setBalance(wallet.getBalance());
        dto.setAvailableBalance(wallet.getAvailableBalance());
        return dto;
    }
}
