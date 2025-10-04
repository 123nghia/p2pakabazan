package com.akabazan.api.mapper;

import com.akabazan.api.reponse.UserResponse;
import com.akabazan.repository.entity.User;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setKycStatus(user.getKycStatus());

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
