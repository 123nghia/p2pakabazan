package com.akabazan.service.dto;

import com.akabazan.repository.entity.FiatAccount;

public final class FiatAccountMapper {

    private FiatAccountMapper() {
    }

    public static FiatAccountResult toResult(FiatAccount entity) {
        FiatAccountResult result = new FiatAccountResult();
        result.setId(entity.getId());
        result.setBankName(entity.getBankName());
        result.setAccountNumber(entity.getAccountNumber());
        result.setAccountHolder(entity.getAccountHolder());
        result.setBranch(entity.getBranch());
        result.setPaymentType(entity.getPaymentType());
        return result;
    }
}
