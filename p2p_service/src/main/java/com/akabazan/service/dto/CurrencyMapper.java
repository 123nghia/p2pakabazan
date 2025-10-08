package com.akabazan.service.dto;

import com.akabazan.repository.entity.Currency;

public final class CurrencyMapper {

    private CurrencyMapper() {
    }

    public static CurrencyResult toResult(Currency currency) {
        CurrencyResult result = new CurrencyResult();
        result.setId(currency.getId());
        result.setType(currency.getType());
        result.setCode(currency.getCode());
        result.setName(currency.getName());
        result.setNetwork(currency.getNetwork());
        result.setIconUrl(currency.getIconUrl());
        result.setDecimalPlaces(currency.getDecimalPlaces());
        result.setDisplayOrder(currency.getDisplayOrder());
        result.setActive(currency.isActive());
        return result;
    }
}
