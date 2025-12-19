package com.akabazan.partner.internal.funds;

import java.util.List;

public record BalancesResponse(String externalUserId, List<AssetBalance> balances) {

    public static BalancesResponse single(String externalUserId, String asset, java.math.BigDecimal available, java.math.BigDecimal locked) {
        return new BalancesResponse(externalUserId, List.of(new AssetBalance(
                asset,
                available == null ? "0" : available.stripTrailingZeros().toPlainString(),
                locked == null ? "0" : locked.stripTrailingZeros().toPlainString()
        )));
    }

    public record AssetBalance(String asset, String available, String locked) {}
}

