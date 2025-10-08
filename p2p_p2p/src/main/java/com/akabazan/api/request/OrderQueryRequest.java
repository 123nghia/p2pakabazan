package com.akabazan.api.request;

import com.akabazan.common.dto.BaseQueryRequest;

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
