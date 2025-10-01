package com.akabazan.api.controller;

import com.akabazan.api.dto.WalletBalanceResponse;
import com.akabazan.api.mapper.WalletBalanceResponseMapper;
import com.akabazan.service.WalletBalanceService;
import com.akabazan.service.dto.WalletBalanceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/p2p")
@CrossOrigin(origins = "http://localhost:5500")
public class WalletController {

    private final WalletBalanceService walletBalanceService;

    public WalletController(WalletBalanceService walletBalanceService) {
        this.walletBalanceService = walletBalanceService;
    }

    @GetMapping("/wallets")
    public ResponseEntity<List<WalletBalanceResponse>> getWalletBalances() {
        List<WalletBalanceResult> balances = walletBalanceService.getCurrentUserBalances();
        return ResponseEntity.ok(WalletBalanceResponseMapper.fromList(balances));
    }
}
