package com.akabazan.service;

import com.akabazan.service.dto.WalletBalanceDTO;

import java.util.List;

public interface WalletBalanceService {

    List<WalletBalanceDTO> getCurrentUserBalances();
}
