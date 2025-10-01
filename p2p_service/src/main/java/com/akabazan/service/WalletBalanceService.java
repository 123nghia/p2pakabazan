package com.akabazan.service;

import com.akabazan.service.dto.WalletBalanceResult;

import java.util.List;

public interface WalletBalanceService {

    List<WalletBalanceResult> getCurrentUserBalances();
}
