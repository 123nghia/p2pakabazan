package com.akabazan.service.dto;

public class BinanceP2PRequest {
    private String asset;        // token: USDT, BTC...
    private String fiat;         // VND, USD...
    private Integer page;        // mặc định 1
    private Integer rows;        // số kết quả mỗi page (10-20)
    private String tradeType;    // BUY hoặc SELL
    private Boolean merchantCheck;
    private Object payTypes;     // null nếu lấy all
    private Object publisherType;// null nếu lấy all

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public String getFiat() { return fiat; }
    public void setFiat(String fiat) { this.fiat = fiat; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getRows() { return rows; }
    public void setRows(Integer rows) { this.rows = rows; }

    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }

    public Boolean getMerchantCheck() { return merchantCheck; }
    public void setMerchantCheck(Boolean merchantCheck) { this.merchantCheck = merchantCheck; }

    public Object getPayTypes() { return payTypes; }
    public void setPayTypes(Object payTypes) { this.payTypes = payTypes; }

    public Object getPublisherType() { return publisherType; }
    public void setPublisherType(Object publisherType) { this.publisherType = publisherType; }

}
