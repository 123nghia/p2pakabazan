package com.akabazan.api.mapper;

import com.akabazan.api.dto.UserDTO;
import com.akabazan.repository.entity.User;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toDto(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setKycStatus(user.getKycStatus());

        dto.setWallets(user.getWallets().stream().map(wallet -> {
            UserDTO.WalletDTO w = new UserDTO.WalletDTO();
            w.setToken(wallet.getToken());
            w.setAddress(wallet.getAddress());
            w.setBalance(wallet.getBalance());
            return w;
        }).collect(Collectors.toList()));

        dto.setLoginHistory(user.getLoginHistory().stream().map(login -> {
            UserDTO.LoginHistoryDTO l = new UserDTO.LoginHistoryDTO();
            l.setIp(login.getIp());
            l.setDevice(login.getDevice());
            l.setTimestamp(login.getTimestamp());
            return l;
        }).collect(Collectors.toList()));

        return dto;
    }
}
