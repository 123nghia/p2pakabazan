package com.akabazan.api.mapper;

import com.akabazan.api.dto.WalletBalanceResponse;
import com.akabazan.service.dto.WalletBalanceResult;
import java.util.List;
import java.util.stream.Collectors;

public final class WalletBalanceResponseMapper {

    private WalletBalanceResponseMapper() {
    }

    public static WalletBalanceResponse from(WalletBalanceResult result) {
        if (result == null) {
            return null;
        }
        WalletBalanceResponse response = new WalletBalanceResponse();
        response.setToken(result.getToken());
        response.setBalance(result.getBalance());
        response.setAvailableBalance(result.getAvailableBalance());
        return response;
    }

    public static List<WalletBalanceResponse> fromList(List<WalletBalanceResult> results) {
        return results.stream()
                .map(WalletBalanceResponseMapper::from)
                .collect(Collectors.toList());
    }
}
