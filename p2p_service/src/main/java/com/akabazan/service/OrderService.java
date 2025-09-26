package com.akabazan.service;

import com.akabazan.service.dto.OrderDTO;
import com.akabazan.service.dto.TradeDTO;
import com.akabazan.service.dto.ChatMessageDTO;

import java.util.List;

public interface OrderService {

    /**
     * Tạo một order mới
     */
    OrderDTO createOrder(OrderDTO orderDTO);

    /**
     * Lấy danh sách order theo filter
     */
    List<OrderDTO> getOrders(String type, String token, String paymentMethod, String sortByPrice);

    /**
     * Tạo trade từ một order
     */
    TradeDTO createTrade(TradeDTO tradeDTO);

    /**
     * Xác nhận đã thanh toán trade
     */
    TradeDTO confirmPayment(Long tradeId);

    /**
     * Xác nhận đã nhận hàng/tài sản trong trade
     */
    TradeDTO confirmReceived(Long tradeId);

    /**
     * Gửi tin nhắn chat trong trade
     */
    TradeDTO sendChatMessage(Long tradeId, ChatMessageDTO messageDTO);

    /**
     * Mở tranh chấp trong trade
     */
    TradeDTO openDispute(Long tradeId, String reason, String evidence);
}
