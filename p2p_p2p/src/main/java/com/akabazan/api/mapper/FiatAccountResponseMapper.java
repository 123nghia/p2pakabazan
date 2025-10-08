package com.akabazan.api.mapper;

import com.akabazan.api.reponse.FiatAccountResponse;
import com.akabazan.service.dto.FiatAccountResult;
import java.util.List;
import java.util.stream.Collectors;

public final class FiatAccountResponseMapper {

    private FiatAccountResponseMapper() {
    }

    public static FiatAccountResponse from(FiatAccountResult result) {
        FiatAccountResponse response = new FiatAccountResponse();
        response.setId(result.getId());
        response.setBankName(result.getBankName());
        response.setAccountNumber(result.getAccountNumber());
        response.setAccountHolder(result.getAccountHolder());
        response.setBranch(result.getBranch());
        response.setPaymentType(result.getPaymentType());
        return response;
    }

    public static List<FiatAccountResponse> fromList(List<FiatAccountResult> results) {
        return results.stream()
                .map(FiatAccountResponseMapper::from)
                .collect(Collectors.toList());
    }
}
