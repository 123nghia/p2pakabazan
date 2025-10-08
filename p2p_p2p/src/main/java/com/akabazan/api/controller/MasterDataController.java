package com.akabazan.api.controller;

import com.akabazan.api.mapper.CurrencyResponseMapper;
import com.akabazan.api.mapper.PaymentMethodResponseMapper;
import com.akabazan.api.reponse.CurrencyResponse;
import com.akabazan.api.reponse.PaymentMethodResponse;
import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.dto.BaseResponse;
import com.akabazan.common.dto.ResponseFactory;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.constant.CurrencyType;
import com.akabazan.repository.constant.PaymentMethodType;
import com.akabazan.service.MasterDataService;
import com.akabazan.service.dto.CurrencyResult;
import com.akabazan.service.dto.PaymentMethodResult;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/p2p/masterdata")
@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://localhost:5174"
})
public class MasterDataController extends BaseController {

    private final MasterDataService masterDataService;

    public MasterDataController(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }

    @GetMapping("/currencies")
    public ResponseEntity<BaseResponse<List<CurrencyResponse>>> getCurrencies(
            @RequestParam(value = "type", required = false) String typeParam) {
        List<CurrencyResult> results;
        if (typeParam == null || typeParam.isBlank()) {
            results = masterDataService.getActiveCurrencies();
        } else {
            CurrencyType type = parseType(typeParam);
            results = masterDataService.getCurrenciesByType(type);
        }
        return ResponseFactory.ok(CurrencyResponseMapper.fromList(results));
    }

    @GetMapping("/currencies/grouped")
    public ResponseEntity<BaseResponse<Map<String, List<CurrencyResponse>>>> getGroupedCurrencies() {
        Map<CurrencyType, List<CurrencyResult>> grouped = masterDataService.getActiveCurrenciesGrouped();
        Map<String, List<CurrencyResponse>> responseData = grouped.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        entry -> CurrencyResponseMapper.fromList(entry.getValue()),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
        return ResponseFactory.ok(responseData);
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<BaseResponse<List<PaymentMethodResponse>>> getPaymentMethods(
            @RequestParam(value = "type", required = false) String typeParam) {
        List<PaymentMethodResult> results;
        if (typeParam == null || typeParam.isBlank()) {
            results = masterDataService.getPaymentMethods();
        } else {
            PaymentMethodType type = parsePaymentMethodType(typeParam);
            results = masterDataService.getPaymentMethodsByType(type);
        }
        return ResponseFactory.ok(PaymentMethodResponseMapper.fromList(results));
    }

    private CurrencyType parseType(String value) {
        try {
            return CurrencyType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ApplicationException(ErrorCode.INVALID_CURRENCY_TYPE);
        }
    }

    private PaymentMethodType parsePaymentMethodType(String value) {
        try {
            return PaymentMethodType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ApplicationException(ErrorCode.INVALID_PAYMENT_METHOD_TYPE);
        }
    }
}
