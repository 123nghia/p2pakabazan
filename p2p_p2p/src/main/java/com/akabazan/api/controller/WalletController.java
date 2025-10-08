package com.akabazan.api.controller;

import com.akabazan.api.mapper.WalletBalanceResponseMapper;
import com.akabazan.api.reponse.WalletBalanceResponse;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.WalletBalanceService;
import com.akabazan.service.dto.WalletBalanceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/p2p")
public class WalletController extends BaseController {

    private final WalletBalanceService walletBalanceService;

    public WalletController(WalletBalanceService walletBalanceService) {
        this.walletBalanceService = walletBalanceService;
    }

    @GetMapping("/wallets")
    public ResponseEntity<BaseResponse<List<WalletBalanceResponse>>> getWalletBalances() {
        List<WalletBalanceResult> balances = walletBalanceService.getCurrentUserBalances();
        return ResponseFactory.ok(WalletBalanceResponseMapper.fromList(balances));
    }
}
