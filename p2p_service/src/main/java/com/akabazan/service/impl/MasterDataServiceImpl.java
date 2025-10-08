package com.akabazan.service.impl;

import com.akabazan.repository.CurrencyRepository;
import com.akabazan.repository.PaymentMethodRepository;
import com.akabazan.repository.constant.CurrencyType;
import com.akabazan.repository.constant.PaymentMethodType;
import com.akabazan.service.MasterDataService;
import com.akabazan.service.dto.CurrencyMapper;
import com.akabazan.service.dto.CurrencyResult;
import com.akabazan.service.dto.PaymentMethodMapper;
import com.akabazan.service.dto.PaymentMethodResult;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MasterDataServiceImpl implements MasterDataService {

    private final CurrencyRepository currencyRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public MasterDataServiceImpl(CurrencyRepository currencyRepository,
                                 PaymentMethodRepository paymentMethodRepository) {
        this.currencyRepository = currencyRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public List<CurrencyResult> getCurrenciesByType(CurrencyType type) {
        return currencyRepository.findAllByTypeAndActiveTrueOrderByDisplayOrderAscCodeAsc(type)
                .stream()
                .map(CurrencyMapper::toResult)
                .collect(Collectors.toList());
    }

    @Override
    public List<CurrencyResult> getActiveCurrencies() {
        return currencyRepository.findAllByActiveTrueOrderByDisplayOrderAscCodeAsc()
                .stream()
                .map(CurrencyMapper::toResult)
                .collect(Collectors.toList());
    }

    @Override
    public Map<CurrencyType, List<CurrencyResult>> getActiveCurrenciesGrouped() {
        Map<CurrencyType, List<CurrencyResult>> grouped = new EnumMap<>(CurrencyType.class);
        grouped.put(CurrencyType.TOKEN, getCurrenciesByType(CurrencyType.TOKEN));
        grouped.put(CurrencyType.FIAT, getCurrenciesByType(CurrencyType.FIAT));
        return grouped;
    }

    @Override
    public List<PaymentMethodResult> getPaymentMethods() {
        return paymentMethodRepository.findAllByActiveTrueOrderByDisplayOrderAscNameAsc()
                .stream()
                .map(PaymentMethodMapper::toResult)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentMethodResult> getPaymentMethodsByType(PaymentMethodType type) {
        return paymentMethodRepository.findAllByTypeAndActiveTrueOrderByDisplayOrderAscNameAsc(type)
                .stream()
                .map(PaymentMethodMapper::toResult)
                .collect(Collectors.toList());
    }
}
