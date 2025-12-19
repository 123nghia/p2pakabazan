# Binance P2P Market Service

Tài liệu mô tả luồng xử lý và các tham số của `BinanceP2PMarketService`, lớp đảm nhiệm việc lấy giá P2P từ Binance trong hệ thống.

## Môi trường & Ngôn ngữ

- **Ngôn ngữ triển khai:** Java 17
- **Framework:** Spring Boot 3.1.x, module `p2p_service`
- **Build tool:** Maven
- **Thư viện chính:** `RestTemplate`, Jackson, GZIP utilities
- **Tài liệu:** tiếng Việt

## Cách chạy nhanh

1. Bật PostgreSQL và cập nhật `application.properties` nếu cần.
2. Chạy lệnh `mvn clean install` ở root để build toàn bộ modules.
3. Khởi động backend: `mvn -pl p2p_p2p spring-boot:run`.
4. Endpoint sử dụng service này: `GET /api/market/price` (hoặc các API lấy giá).


## Mục tiêu

- Gửi yêu cầu POST tới Binance P2P API để truy vấn danh sách quảng cáo mua/bán.
- Xử lý phản hồi (có thể được nén gzip) và chọn ra giá phù hợp nhất dựa trên `tradeType` và giới hạn `top`.
- Cung cấp giá tổng hợp cho các controller (`MarketController`, `P2PController`).

## Endpoint Binance được sử dụng

```
POST https://p2p.binance.com/bapi/c2c/v2/friendly/c2c/adv/search
```

## Tham số đầu vào của service

| Tên       | Kiểu    | Bắt buộc | Ghi chú |
|-----------|---------|----------|---------|
| `token`   | String  | Có       | Mã token crypto (ví dụ: `USDT`). Mapped vào `asset` trong request body. |
| `fiat`    | String  | Có       | Mã tiền pháp định (ví dụ: `VND`). |
| `tradeType` | String | Có      | `BUY` hoặc `SELL`. Được uppercase trước khi gửi sang Binance. |
| `top`     | int     | Có       | Số lượng quảng cáo tối đa cần đọc để tính toán giá. |

## Payload gửi lên Binance

```json
{
  "asset": "USDT",
  "fiat": "VND",
  "page": 1,
  "rows": 20,
  "tradeType": "SELL",
  "merchantCheck": false
}
```

> Lưu ý: service luôn gửi `rows = 20` và cắt kết quả theo `top` (min(top, data.length)).

## Header quan trọng

- `Content-Type: application/json`
- `User-Agent: Mozilla/5.0` (tránh bị block request)
- `Accept-Encoding: gzip, deflate` (API thường trả về gzip)

## Quy trình xử lý phản hồi

1. Kiểm tra HTTP status code 2xx. Nếu không, throw `RuntimeException` với thông báo "Không gọi được Binance P2P API".
2. Xác thực body không rỗng.
3. Giải nén response nếu header `Content-Encoding` là `gzip`.
4. Loại bỏ các ký tự control (regex `[\x00-\x1F]`) trước khi parse JSON.
5. Parse JSON bằng `ObjectMapper`:
   - Lấy mảng `data` chứa các quảng cáo.
   - Lặp qua tối đa `top` phần tử đầu tiên, đọc `adv.price` để tính giá:
     - `SELL`: chọn **max** để lấy giá bán cao nhất.
     - `BUY`: chọn **min** để lấy giá mua thấp nhất.
6. Nếu không tìm thấy giá, throw `RuntimeException` "Không lấy được giá từ Binance P2P".
7. Bắt mọi exception còn lại, log stack trace, trả về `null` để controller có thể xử lý tiếp.

## Trả về từ `getP2PPrice`

- Giá tốt nhất (double) theo tiêu chí trên.
- `null` nếu gặp lỗi (controller cần kiểm tra `null`).

## Các điểm cần chú ý

- Hiện tại service tạo `RestTemplate` mới cho mỗi request; có thể refactor để tái sử dụng bean được inject qua constructor.
- Nên thêm logging/thống kê để theo dõi tỷ lệ lỗi khi gọi Binance.
- Khi `top` lớn hơn số quảng cáo thực tế, service tự động cắt về `data.size()`.
- Cần đảm bảo giá trị `tradeType` hợp lệ (`BUY`/`SELL`), nếu không API Binance sẽ trả lỗi.
- Với tải lớn, cân nhắc cơ chế cache/tần suất gọi lại Binance để tránh rate limit.

## Ví dụ sử dụng trong controller

```java
@GetMapping("/price")
public ResponseEntity<Double> getPrice(
        @RequestParam String token,
        @RequestParam String fiat,
        @RequestParam(defaultValue = "SELL") String tradeType,
        @RequestParam(defaultValue = "5") int top) {

    Double price = marketService.getP2PPrice(token, fiat, tradeType, top);
    return price != null ? ResponseEntity.ok(price)
                         : ResponseEntity.status(502).body(null);
}
```

## Đề xuất mở rộng

- Chuẩn hóa thông báo lỗi trả về cho FE (ví dụ sử dụng enum hoặc mã lỗi chuẩn).
- Bổ sung unit test với response mẫu từ Binance (đặc biệt là case gzip).
- Thêm tuỳ chọn `merchantCheck` từ input để lọc theo merchant đã xác thực.
- Bổ sung timeout/retry cho `RestTemplate` để tránh treo khi Binance chậm.
