package com.akabazan.api.controller;

import com.akabazan.api.mapper.FiatAccountResponseMapper;
import com.akabazan.api.reponse.FiatAccountResponse;
import com.akabazan.api.request.FiatAccountRequest;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.service.FiatAccountService;
import com.akabazan.service.dto.FiatAccountResult;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/p2p/fiat-accounts")
public class FiatAccountController extends BaseController {

    private final FiatAccountService fiatAccountService;

    public FiatAccountController(FiatAccountService fiatAccountService) {
        this.fiatAccountService = fiatAccountService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<FiatAccountResponse>>> getMyFiatAccounts() {
        List<FiatAccountResult> accounts = fiatAccountService.getCurrentUserAccounts();
        return ResponseFactory.ok(FiatAccountResponseMapper.fromList(accounts));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<FiatAccountResponse>> createFiatAccount(
            @RequestBody FiatAccountRequest request) {
        FiatAccountResult payload = new FiatAccountResult();
        payload.setBankName(request.getBankName());
        payload.setAccountNumber(request.getAccountNumber());
        payload.setAccountHolder(request.getAccountHolder());
        payload.setBranch(request.getBranch());
        payload.setPaymentType(request.getPaymentType());

        FiatAccountResult created = fiatAccountService.createFiatAccount(payload);
        return ResponseFactory.ok(FiatAccountResponseMapper.from(created));
    }
}
