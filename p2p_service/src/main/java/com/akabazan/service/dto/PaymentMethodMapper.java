package com.akabazan.service.dto;

import com.akabazan.repository.entity.PaymentMethod;

public final class PaymentMethodMapper {

    private PaymentMethodMapper() {
    }

    public static PaymentMethodResult toResult(PaymentMethod entity) {
        PaymentMethodResult result = new PaymentMethodResult();
        result.setId(entity.getId());
        result.setType(entity.getType());
        result.setCode(entity.getCode());
        result.setName(entity.getName());
        result.setDescription(entity.getDescription());
        result.setIconUrl(entity.getIconUrl());
        result.setDisplayOrder(entity.getDisplayOrder());
        result.setActive(entity.isActive());
        return result;
    }
}
