# So sánh P2P (Akabazan) với mô hình khác

> Tài liệu rút gọn để tham chiếu khi trao đổi với đối tác/sản phẩm. Không nêu tên vendor cụ thể, dùng nhóm tính năng phổ biến.

## Phạm vi so sánh
- **P2P Akabazan**: hệ thống hiện tại trong repo (SSO partner, funds API partner).
- **P2P sàn lớn**: mô hình P2P phổ biến (escrow nội bộ, user giữ tiền trong sàn đó).
- **OTC thủ công**: nhóm chat/tele, chuyển khoản trực tiếp, không escrow tự động.
- **Orderbook CEX**: giao ngay truyền thống, khớp lệnh sổ lệnh, không OTC giữa người dùng.

## Bảng so sánh nhanh
- **Giữ tiền**  
  - P2P Akabazan: tiền vẫn ở ví đối tác, P2P khóa/mở/chuyển qua Funds API.  
  - P2P sàn lớn: tiền nằm trong ví sàn; sàn kiểm soát escrow nội bộ.  
  - OTC thủ công: tiền chuyển trực tiếp giữa người dùng, không escrow.  
  - Orderbook CEX: tiền trong ví sàn, khớp lệnh sổ lệnh.

- **Onboarding/SSO**  
  - P2P Akabazan: SSO bằng code (HMAC) từ partner → P2P, không yêu cầu login lại.  
  - P2P sàn lớn: login tại chính sàn đó; không cần SSO từ ngoài.  
  - OTC thủ công: không.  
  - Orderbook CEX: login tại sàn; không SSO từ ngoài.

- **Escrow & an toàn giao dịch**  
  - P2P Akabazan: lock/unlock/transfer qua API partner, idempotent theo `requestId`.  
  - P2P sàn lớn: lock nội bộ, quy trình tranh chấp rõ, tự giữ tiền.  
  - OTC thủ công: không escrow, rủi ro cao.  
  - Orderbook CEX: không OTC, khớp lệnh spot; không cần escrow.

- **Chat & thông báo**  
  - P2P Akabazan: chat trong trade, WebSocket 9002, event theo trạng thái trade.  
  - P2P sàn lớn: có chat và notification app.  
  - OTC thủ công: chat ngoại bộ (zalo/tele), không kiểm soát.  
  - Orderbook CEX: không chat.

- **Dispute/verify**  
  - P2P Akabazan: workflow tranh chấp cơ bản (có hook), cần hoàn thiện vận hành.  
  - P2P sàn lớn: đội vận hành mạnh, quy trình chuẩn.  
  - OTC thủ công: gần như không có.  
  - Orderbook CEX: không cần tranh chấp giữa user.

- **API tích hợp**  
  - P2P Akabazan: có Integration API (sync user/wallet), Funds API yêu cầu partner mở.  
  - P2P sàn lớn: public API cho order/trade, nhưng không mở để cắm ví ngoài.  
  - OTC thủ công: không API.  
  - Orderbook CEX: API đầy đủ (REST/WebSocket) nhưng không phải P2P escrow.

- **KYC/Compliance**  
  - P2P Akabazan: dùng KYC của partner, truyền `kycStatus` qua SSO.  
  - P2P sàn lớn: KYC do sàn thực hiện.  
  - OTC thủ công: không.  
  - Orderbook CEX: KYC do sàn thực hiện.

- **Thời gian tích hợp**  
  - P2P Akabazan: cần triển khai 2 phần ở partner (SSO + Funds API), ước tính 1–2 tuần dev/backend nếu hạ tầng sẵn.  
  - P2P sàn lớn: không tích hợp, dùng trực tiếp.  
  - OTC thủ công: không tích hợp, nhưng rủi ro cao.  
  - Orderbook CEX: không liên quan P2P.

- **Trải nghiệm người dùng**  
  - P2P Akabazan: liền mạch với partner (SSO, giữ tiền tại chỗ), giao diện P2P riêng.  
  - P2P sàn lớn: trải nghiệm trong một sàn thống nhất.  
  - OTC thủ công: phụ thuộc ứng dụng chat, không có bảo vệ.  
  - Orderbook CEX: phù hợp trader, không phù hợp OTC fiat giữa người dùng.

## Điểm mạnh của P2P Akabazan
- Không yêu cầu người dùng nạp rút sang P2P; giữ tiền tại partner → ít ma sát.  
- Luồng SSO rõ ràng, chống replay bằng timestamp + nonce + Redis.  
- Funds API tách biệt, idempotent theo `requestId`; dễ audit.  
- Hạ tầng docker kèm partner_mock để demo/POC nhanh (port 9000/9001).

## Khoảng trống / cần lưu ý
- Quy trình vận hành tranh chấp, KYC nâng cao, và các hạn mức AML phụ thuộc partner; cần làm rõ trước go-live.  
- Partner phải đầu tư dev cho Funds API (balances/lock/unlock/transfer) và quản trị secret HMAC.  
- Hiệu năng/độ trễ phụ thuộc API partner; nên có SLA, timeout/retry và giám sát.  
- UX/CX: cần đồng bộ brand, CORS/origin whitelist và ngôn ngữ theo partner.  
