package com.akabazan.repository.constant;

public enum TradeStatus {
    PENDING, // tạo trade nhưng chưa thanh toán
    PAID, // buyer xác nhận đã thanh toán
    CONFIRMED, // seller xác nhận đã nhận tiền
    DISPUTED, // tranh chấp
    COMPLETED, // giao dịch hoàn tất,
    CANCELLED // tạo trade nhưng chưa thanh toán
}
