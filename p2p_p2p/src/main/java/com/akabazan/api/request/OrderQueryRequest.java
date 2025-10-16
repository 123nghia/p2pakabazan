package com.akabazan.api.request;

import com.akabazan.common.dto.BaseQueryRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderQueryRequest extends BaseQueryRequest {

    private String type;
    private String token;
    private String paymentMethod;
    private String sortByPrice;
    private String fiat;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<String> getPaymentMethods() {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            return List.of();
        }
        return Arrays.stream(paymentMethod.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .distinct()
                .collect(Collectors.toList());
    }

    public String getSortByPrice() {
        return sortByPrice;
    }

    public void setSortByPrice(String sortByPrice) {
        this.sortByPrice = sortByPrice;
    }

    public String getFiat() {
        return fiat;
    }

    public void setFiat(String fiat) {
        this.fiat = fiat;
    }

}
