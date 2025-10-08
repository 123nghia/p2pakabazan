package com.akabazan.api.mapper;

import com.akabazan.api.reponse.CurrencyResponse;
import com.akabazan.service.dto.CurrencyResult;
import java.util.List;
import java.util.stream.Collectors;

public final class CurrencyResponseMapper {

    private CurrencyResponseMapper() {
    }

    public static CurrencyResponse from(CurrencyResult result) {
        CurrencyResponse response = new CurrencyResponse();
        response.setId(result.getId());
        response.setType(result.getType().name());
        response.setCode(result.getCode());
        response.setName(result.getName());
        response.setNetwork(result.getNetwork());
        response.setIconUrl(result.getIconUrl());
        response.setDecimalPlaces(result.getDecimalPlaces());
        response.setDisplayOrder(result.getDisplayOrder());
        response.setActive(result.isActive());
        return response;
    }

    public static List<CurrencyResponse> fromList(List<CurrencyResult> results) {
        return results.stream()
                .map(CurrencyResponseMapper::from)
                .collect(Collectors.toList());
    }
}
