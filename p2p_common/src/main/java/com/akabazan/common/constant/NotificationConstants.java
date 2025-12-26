package com.akabazan.common.constant;

public final class NotificationConstants {
    private NotificationConstants() {
    }

    // Dispute Notifications
    public static final String DISPUTE_RESOLVED = "Tranh chấp cho giao dịch #%s đã được giải quyết và %s là bên thắng.";
    public static final String DISPUTE_REJECTED = "Tranh chấp cho giao dịch #%s đã bị từ chối.";
    public static final String DISPUTE_ASSIGNED_ADMIN = "Tranh chấp cho giao dịch #%s đã được phân công cho bạn xử lý.";
    public static final String DISPUTE_IN_REVIEW = "Tranh chấp cho giao dịch #%s đang được %s xử lý.";
    public static final String DISPUTE_OPENED = "Tranh chấp đã được mở cho giao dịch #%s.";

    // Order Notifications
    public static final String ORDER_CREATED_SUCCESS = "Quảng cáo %s của bạn đã được tạo thành công.";

    // Labels
    public static final String BUY = "MUA";
    public static final String SELL = "BÁN";
    public static final String OUTCOME_BUYER = "Người mua";
    public static final String OUTCOME_SELLER = "Người bán";
    public static final String OUTCOME_CANCELLED = "Hủy bỏ";

    // Trade System Messages (Chat)
    public static final String TRADE_CREATED_BUYER = "Hệ thống (gửi người mua): Giao dịch #%s đã được tạo thành công với số lượng %s %s. Vui lòng chờ người bán cung cấp thông tin thanh toán.";
    public static final String TRADE_CREATED_SELLER = "Hệ thống (gửi người bán): Bạn vừa nhận yêu cầu giao dịch #%s với số lượng %s %s. Vui lòng gửi thông tin thanh toán cho người mua.";
    public static final String PAYMENT_CONFIRMED_BUYER = "Hệ thống (gửi người mua): Bạn đã xác nhận đã chuyển tiền cho giao dịch #%s. Vui lòng chờ người bán kiểm tra và giải phóng tài sản.";
    public static final String PAYMENT_CONFIRMED_SELLER = "Hệ thống (gửi người bán): Người mua đã xác nhận đã chuyển tiền cho giao dịch #%s. Vui lòng kiểm tra và xác nhận khi nhận đủ tiền.";
    public static final String TRADE_COMPLETED_BUYER = "Hệ thống (gửi người mua): Người bán đã giải phóng %s %s cho giao dịch #%s. Tài sản đã về ví của bạn.";
    public static final String TRADE_COMPLETED_SELLER = "Hệ thống (gửi người bán): Bạn đã hoàn tất giao dịch #%s và đã giải phóng %s %s cho người mua.";
    public static final String TRADE_CANCELLED_BY_BUYER_FOR_BUYER = "Hệ thống (gửi người mua): Bạn đã hủy giao dịch #%s. Nếu vẫn muốn giao dịch, vui lòng tạo lệnh mới.";
    public static final String TRADE_CANCELLED_BY_BUYER_FOR_SELLER = "Hệ thống (gửi người bán): Người mua đã hủy giao dịch #%s. Số tài sản liên quan đã được hoàn trả.";
    public static final String TRADE_CANCELLED_BY_SELLER_FOR_SELLER = "Hệ thống (gửi người bán): Bạn đã hủy giao dịch #%s. Tài sản đã được trả lại ví của bạn.";
    public static final String TRADE_CANCELLED_BY_SELLER_FOR_BUYER = "Hệ thống (gửi người mua): Người bán đã hủy giao dịch #%s. Bạn có thể chọn lệnh khác để tiếp tục.";
    public static final String TRADE_AUTO_CANCELLED_BUYER = "Hệ thống (gửi người mua): Giao dịch #%s đã bị hủy tự động do quá hạn xử lý. Vui lòng tạo giao dịch mới nếu vẫn có nhu cầu.";
    public static final String TRADE_AUTO_CANCELLED_SELLER = "Hệ thống (gửi người bán): Giao dịch #%s đã bị hủy tự động do quá hạn xử lý. Tài sản tạm giữ đã được giải phóng.";

}
