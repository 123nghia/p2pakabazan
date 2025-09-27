package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.OrderService;
import com.akabazan.service.UserService;
import com.akabazan.service.dto.OrderDTO;
import com.akabazan.service.dto.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final OrderRepository orderRepository;
   

    public OrderServiceImpl(UserRepository userRepository,
                            WalletRepository walletRepository,
                            OrderRepository orderRepository
                            ) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.orderRepository = orderRepository;
      
    }

    private User getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }
    @Override
    @Transactional
   public OrderDTO createOrder(OrderDTO orderDTO) {
    User user = getCurrentUser();
     orderDTO.setType("SELL");
    if (user.getKycStatus() != User.KycStatus.VERIFIED)
        throw new ApplicationException(ErrorCode.KYC_REQUIRED);


    // Chỉ SELL mới được tạo order
    // if (!"SELL".equalsIgnoreCase(orderDTO.getType())) {
    //     throw new ApplicationException(ErrorCode.INVALID_ORDER_TYPE);
    // }

    // Lấy ví và check balance
    Wallet wallet = walletRepository.lockByUserIdAndToken(user.getId(), orderDTO.getToken())
            .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));

    if (wallet.getAvailableBalance() < orderDTO.getAmount())
        throw new ApplicationException(ErrorCode.INSUFFICIENT_BALANCE);

    // Lock số dư
    wallet.setAvailableBalance(wallet.getAvailableBalance() - orderDTO.getAmount());
    walletRepository.save(wallet);

    // Tạo order
    Order order = new Order();
    order.setUser(user);
    order.setType("SELL"); // mặc định uppercase
    order.setToken(orderDTO.getToken());
    order.setAmount(orderDTO.getAmount());
    order.setAvailableAmount(orderDTO.getAmount());
    order.setPrice(orderDTO.getPrice());
    order.setMinLimit(orderDTO.getMinLimit());
    order.setMaxLimit(orderDTO.getMaxLimit());
    order.setPaymentMethod(orderDTO.getPaymentMethod());
    order.setFiatAccount(orderDTO.getFiatAccount());
    order.setStatus(OrderStatus.OPEN.name());
    order.setExpireAt(LocalDateTime.now().plusMinutes(15));

    return OrderMapper.toDTO(orderRepository.save(order));
}

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        User seller = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(seller.getId()))
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);

        if (!OrderStatus.OPEN.name().equals(order.getStatus()))
            throw new ApplicationException(ErrorCode.ORDER_CLOSED);

        // Hoàn lại availableBalance nếu là SELL order
        if ("SELL".equalsIgnoreCase(order.getType()) && order.getAvailableAmount() > 0) {
            Wallet wallet = walletRepository.findByUserIdAndToken(seller.getId(), order.getToken())
                    .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));
            wallet.setAvailableBalance(wallet.getAvailableBalance() + order.getAvailableAmount());
            walletRepository.save(wallet);
        }

        order.setStatus(OrderStatus.CANCELLED.name());
        orderRepository.save(order);
    }

    @Transactional
    public void expireOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> expiredOrders = orderRepository.findAllByStatusAndExpireAtBefore(OrderStatus.OPEN.name(), now);

        for (Order order : expiredOrders) {
            if ("SELL".equalsIgnoreCase(order.getType()) && order.getAvailableAmount() > 0) {
                Wallet wallet = walletRepository.findByUserIdAndToken(order.getUser().getId(), order.getToken())
                        .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));
                wallet.setAvailableBalance(wallet.getAvailableBalance() + order.getAvailableAmount());
                walletRepository.save(wallet);
            }

            order.setStatus(OrderStatus.EXPIRED.name());
            orderRepository.save(order);
        }
    }

    @Override
    @Transactional
    public void closeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND));

        if ("SELL".equalsIgnoreCase(order.getType()) && order.getAvailableAmount() > 0) {
            Wallet wallet = walletRepository.lockByUserIdAndToken(order.getUser().getId(), order.getToken())
                    .orElseThrow(() -> new ApplicationException(ErrorCode.WALLET_NOT_FOUND));
            wallet.setAvailableBalance(wallet.getAvailableBalance() + order.getAvailableAmount());
            walletRepository.save(wallet);
        }

        order.setStatus(OrderStatus.CLOSED.name());
        orderRepository.save(order);
    }

    @Override
    public List<OrderDTO> getOrders(String type, String token, String paymentMethod, String sortByPrice) {
        List<Order> orders = orderRepository.findByStatusAndTypeAndTokenAndPaymentMethod(
                OrderStatus.OPEN.name(),
                type != null ? type.toUpperCase() : null,
                token,
                paymentMethod
        );

        if ("asc".equalsIgnoreCase(sortByPrice)) {
            orders.sort((o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));
        } else if ("desc".equalsIgnoreCase(sortByPrice)) {
            orders.sort((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
        }

        return orders.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

}
