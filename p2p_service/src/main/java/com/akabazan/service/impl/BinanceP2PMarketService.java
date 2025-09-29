package com.akabazan.service.impl;

import com.akabazan.service.MarketService;
import com.akabazan.service.dto.BinanceP2PRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BinanceP2PMarketService implements MarketService {

    private static final String BINANCE_P2P_API =
            "https://p2p.binance.com/bapi/c2c/v2/friendly/c2c/adv/search";

    private final RestTemplate restTemplate;

    public BinanceP2PMarketService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Double getP2PPrice(String token, String fiat, String tradeType, int top) {
    try {
        BinanceP2PRequest req = new BinanceP2PRequest();
        req.setAsset(token);
        req.setFiat(fiat);
        req.setPage(1);
        req.setRows(20);
        req.setTradeType(tradeType.toUpperCase());
        req.setMerchantCheck(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0");
        headers.set("Accept-Encoding", "gzip, deflate");

        HttpEntity<BinanceP2PRequest> entity = new HttpEntity<>(req, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<byte[]> resp = restTemplate.exchange(
                "https://p2p.binance.com/bapi/c2c/v2/friendly/c2c/adv/search",
                HttpMethod.POST,
                entity,
                byte[].class
        );

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Không gọi được Binance P2P API");
        }

        byte[] bodyBytes = resp.getBody();
        if (bodyBytes == null || bodyBytes.length == 0) {
            throw new RuntimeException("Response Binance P2P rỗng");
        }

        String jsonStr;
        if ("gzip".equalsIgnoreCase(resp.getHeaders().getFirst("Content-Encoding"))) {
            try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bodyBytes));
                 InputStreamReader isr = new InputStreamReader(gis, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                jsonStr = sb.toString();
            }
        } else {
            jsonStr = new String(bodyBytes, StandardCharsets.UTF_8);
        }

        // Loại bỏ ký tự control lạ
        jsonStr = jsonStr.replaceAll("[\\x00-\\x1F]", "");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode body = mapper.readTree(jsonStr);

        JsonNode dataArr = body.path("data");
        if (!dataArr.isArray() || dataArr.size() == 0) {
            throw new RuntimeException("Không có quảng cáo P2P");
        }

        int limit = Math.min(top, dataArr.size());
        Double bestPrice = null;

        for (int i = 0; i < limit; i++) {
            JsonNode adv = dataArr.get(i).path("adv");
            if (adv.has("price")) {
                double price = adv.get("price").asDouble();
                if (bestPrice == null) {
                    bestPrice = price;
                } else {
                    // Nếu tradeType = SELL, muốn bán cao → chọn max
                    // Nếu tradeType = BUY, muốn mua rẻ → chọn min
                    if ("SELL".equalsIgnoreCase(tradeType)) {
                        bestPrice = Math.max(bestPrice, price);
                    } else {
                        bestPrice = Math.min(bestPrice, price);
                    }
                }
            }
        }

        if (bestPrice == null) {
            throw new RuntimeException("Không lấy được giá từ Binance P2P");
        }

        return bestPrice;

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}


}