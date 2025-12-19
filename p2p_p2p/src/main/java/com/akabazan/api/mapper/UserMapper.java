package com.akabazan.api.mapper;

import com.akabazan.api.reponse.UserResponse;
import com.akabazan.repository.entity.User;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean isPartnerUser(User user) {
        if (user == null) {
            return false;
        }
        // Native P2P user => type=NULL and relId=NULL.
        // Any non-blank value indicates an external/partner-sourced user.
        return !isBlank(user.getType()) || !isBlank(user.getRelId());
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setType(user.getType());
        dto.setRelId(user.getRelId());
        dto.setKycStatus(user.getKycStatus());

        // Partner users: only return identity info coming from partner (wallets are provisioned later).
        if (isPartnerUser(user)) {
            return dto;
        }

        dto.setWallets(user.getWallets().stream().map(wallet -> {
            UserResponse.WalletResponse w = new UserResponse.WalletResponse();
            w.setToken(wallet.getToken());
            w.setAddress(wallet.getAddress());
            w.setBalance(wallet.getBalance());
            return w;
        }).collect(Collectors.toList()));

        // dto.setLoginHistory(user.getLoginHistory().stream().map(login -> {
        //     UserResponse.LoginHistoryResponse l = new UserResponse.LoginHistoryResponse();
        //     l.setIp(login.getIp());
        //     l.setDevice(login.getDevice());
        //     l.setTimestamp(login.getTimestamp());
        //     return l;
        // }).collect(Collectors.toList()));

        return dto;
    }
}
