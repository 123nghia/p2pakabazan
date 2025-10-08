package com.akabazan.api.mapper;

import com.akabazan.api.reponse.PaymentMethodResponse;
import com.akabazan.service.dto.PaymentMethodResult;
import java.util.List;
import java.util.stream.Collectors;

public final class PaymentMethodResponseMapper {

    private PaymentMethodResponseMapper() {
    }

    public static PaymentMethodResponse from(PaymentMethodResult result) {
        PaymentMethodResponse response = new PaymentMethodResponse();
        response.setId(result.getId());
        response.setType(result.getType().name());
        response.setCode(result.getCode());
        response.setName(result.getName());
        response.setDescription(result.getDescription());
        response.setIconUrl(result.getIconUrl());
        response.setDisplayOrder(result.getDisplayOrder());
        response.setActive(result.isActive());
        return response;
    }

    public static List<PaymentMethodResponse> fromList(List<PaymentMethodResult> results) {
        return results.stream()
                .map(PaymentMethodResponseMapper::from)
                .collect(Collectors.toList());
    }
}
