# Tài liệu chức năng P2P (dành cho tester, không kỹ thuật)

Mục tiêu: giúp tester nắm nhanh hệ thống P2P có những tính năng gì, luồng người dùng ra sao, cần kỳ vọng gì ở mỗi bước. Không đi sâu cấu hình/hạ tầng.

## Nhân vật & bối cảnh
- Người dùng P2P (trader): đăng nhập, đã KYC. Có thể là user nội bộ hoặc user đến từ sàn đối tác (được đưa thẳng vào P2P sau khi bấm “P2P”).
- Đối tác (nếu có): chỉ cần hiểu rằng số dư hiển thị có thể lấy từ ví của họ, nhưng trải nghiệm người dùng vẫn như bình thường trong P2P.

## Tính năng chính
- Xem số dư: màn “Wallet” hiển thị từng token với số dư khả dụng/đang khóa.
- Lên lệnh (Order):
  - Tạo SELL: chọn token, giá, min/max, phương thức nhận tiền (fiat account). Hệ thống khóa lượng token tương ứng. Order mở cho người khác đặt trade.
  - Tạo BUY: chọn token, giá, min/max, phương thức nhận tiền của người bán sẽ được chọn sau (khi trade tạo).
  - Order tự hết hạn nếu quá thời gian (mặc định ~15 phút). Có thể hủy thủ công khi chưa có trade đang xử lý.
- Khớp lệnh/Trade:
  - Người mua chọn một SELL order hoặc người bán chọn một BUY order để tạo trade.
  - Trade giữ trạng thái “PENDING” cho đến khi người mua bấm “Đã thanh toán”.
  - Sau khi người mua báo đã trả, người bán bấm “Đã nhận tiền” để hoàn tất trade; token được giải phóng cho người mua.
  - Trade tự động hủy nếu chờ quá lâu ở trạng thái “PENDING” (mặc định ~15 phút).
- Chat & bằng chứng: mỗi trade có hộp chat; hệ thống tự gửi tin nhắn hệ thống (tạo trade, báo thanh toán, hoàn tất/hủy).
- Khiếu nại (Dispute):
  - Người mua/người bán có thể mở dispute khi trade đang “PAID” nhưng chưa được giải phóng coin.
  - Admin xử lý và chọn kết quả: trả coin cho buyer, trả lại cho seller, hoặc hủy tranh chấp (giữ trạng thái trả về “PAID”).
- Thông báo: người dùng nhận thông báo khi order/trade được tạo, thanh toán, hoàn tất, bị hủy, hoặc khi dispute được cập nhật.

## Luồng trải nghiệm điển hình
1) Đăng nhập/KYC -> vào mục P2P.
2) Xem số dư -> tạo SELL hoặc BUY order (điền token, giá, giới hạn, phương thức thanh toán).
3) Người khác chọn order -> trade được tạo, số dư của seller bị giữ (nếu là SELL) hoặc sẽ được giữ khi seller tham gia BUY order.
4) Người mua chuyển tiền qua kênh fiat đã hiển thị, bấm “Đã thanh toán”.
5) Người bán kiểm tra, bấm “Đã nhận tiền” -> trade hoàn tất, coin được chuyển cho buyer.
6) Nếu có tranh chấp: mở dispute, chờ admin phán quyết (kết quả sẽ tự động giải phóng hoặc trả lại coin).

## Luồng tích hợp đối tác (tóm tắt, không kỹ thuật)
- User đã đăng nhập ở sàn đối tác, bấm “P2P” → được đưa thẳng vào P2P, không phải đăng nhập lại.
- Khi vào P2P, số dư ví hiển thị lấy từ sàn đối tác (không cần nạp riêng vào P2P).
- Khi tạo SELL/BUY và trade, P2P yêu cầu sàn đối tác giữ/giải phóng/chuyển coin tương ứng; người dùng chỉ thấy trải nghiệm bình thường (không thấy bước kỹ thuật).
- Khi trade hoàn tất hoặc bị hủy/dispute, coin được trả hoặc chuyển ngay tại ví đối tác; trạng thái hiển thị trong P2P khớp với kết quả đó.

## Kỳ vọng kiểm thử (gợi ý nhanh)
- Tạo SELL: hệ thống giảm số dư khả dụng đúng với amount, order ở trạng thái OPEN.
- Hủy/expire SELL chưa khớp: số dư khả dụng được trả lại, order đóng.
- Tạo trade từ SELL: số dư seller vẫn đang bị giữ; order available giảm bằng amount trade.
- Buyer bấm “Đã thanh toán”: trade chuyển PAID, seller nhận thông báo.
- Seller bấm “Đã nhận tiền”: trade COMPLETED, buyer thấy token tăng, order đóng nếu hết available.
- Trade tự hủy sau timeout nếu buyer không bấm “Đã thanh toán”; số dư trả về seller, order available tăng lại.
- Dispute BUYER_FAVORED: coin chuyển cho buyer; SELLER_FAVORED: coin trả lại seller.
- Người dùng đối tác: sau khi bấm “P2P” từ sàn đối tác, vào thẳng P2P và thấy số dư đồng nhất với sàn (không phải nạp riêng).
