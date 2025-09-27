package com.akabazan.repository.constant;

public enum OrderStatus {
    OPEN,      // đang mở
    CLOSED,    // đã đóng
    CANCELLED,  // hủy bởi user/admin
    EXPIRED      // Hết hạn theo thời gian
}
