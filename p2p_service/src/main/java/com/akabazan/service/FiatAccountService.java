package com.akabazan.service;

import com.akabazan.service.dto.FiatAccountResult;
import java.util.List;

public interface FiatAccountService {

    List<FiatAccountResult> getCurrentUserAccounts();

    FiatAccountResult createFiatAccount(FiatAccountResult request);
}
