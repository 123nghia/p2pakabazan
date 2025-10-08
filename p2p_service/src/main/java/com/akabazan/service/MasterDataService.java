package com.akabazan.service;

import com.akabazan.repository.constant.CurrencyType;
import com.akabazan.repository.constant.PaymentMethodType;
import com.akabazan.service.dto.CurrencyResult;
import com.akabazan.service.dto.PaymentMethodResult;
import java.util.List;
import java.util.Map;

public interface MasterDataService {

    List<CurrencyResult> getCurrenciesByType(CurrencyType type);

    List<CurrencyResult> getActiveCurrencies();

    Map<CurrencyType, List<CurrencyResult>> getActiveCurrenciesGrouped();

    List<PaymentMethodResult> getPaymentMethods();

    List<PaymentMethodResult> getPaymentMethodsByType(PaymentMethodType type);
}
