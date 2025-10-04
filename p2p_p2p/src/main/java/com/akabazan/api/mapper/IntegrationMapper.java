package com.akabazan.api.mapper;

import com.akabazan.api.reponse.IntegrationUserResponse;
import com.akabazan.api.reponse.UserResponse;
import com.akabazan.api.request.IntegrationUserRequest;
import com.akabazan.repository.entity.User.KycStatus;
import com.akabazan.service.dto.IntegrationSyncCommand;
import com.akabazan.service.dto.IntegrationSyncResult;
import com.akabazan.service.dto.IntegrationWalletData;

public final class IntegrationMapper {

    private IntegrationMapper() {
    }

    public static IntegrationSyncCommand toCommand(IntegrationUserRequest request) {
        IntegrationWalletData walletData = new IntegrationWalletData();
        walletData.setToken(request.getWallet().getToken());
        walletData.setAddress(request.getWallet().getAddress());
        walletData.setBalance(request.getWallet().getBalance());
        walletData.setAvailableBalance(request.getWallet().getAvailableBalance());

        KycStatus kycStatus = null;
        if (request.getKycStatus() != null) {
            kycStatus = KycStatus.valueOf(request.getKycStatus().toUpperCase());
        }

        return new IntegrationSyncCommand(request.getUserId(), walletData, kycStatus);
    }

    public static IntegrationUserResponse toResponse(IntegrationSyncResult result) {
        IntegrationUserResponse response = new IntegrationUserResponse();
        UserResponse userDto = UserMapper.toResponse(result.getUser());
        response.setUser(userDto);
        response.setToken(result.getToken());

        IntegrationUserResponse.WalletResponse walletDto = new IntegrationUserResponse.WalletResponse();
        walletDto.setId(result.getWalletId());
        walletDto.setToken(result.getWalletToken());
        walletDto.setAddress(result.getWalletAddress());
        walletDto.setBalance(result.getWalletBalance());
        walletDto.setAvailableBalance(result.getWalletAvailableBalance());
        response.setWallet(walletDto);

        return response;
    }
}
