package com.akabazan.service;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.TradeChatReadRepository;
import com.akabazan.repository.TradeChatRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.TradeChat;
import com.akabazan.repository.entity.TradeChatRead;
import com.akabazan.service.dto.DashboardCounts;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final CurrentUserService currentUserService;
    private final TradeRepository tradeRepository;
    private final TradeChatRepository tradeChatRepository;
    private final TradeChatReadRepository tradeChatReadRepository;

    public DashboardService(
            CurrentUserService currentUserService,
            TradeRepository tradeRepository,
            TradeChatRepository tradeChatRepository,
            TradeChatReadRepository tradeChatReadRepository) {
        this.currentUserService = currentUserService;
        this.tradeRepository = tradeRepository;
        this.tradeChatRepository = tradeChatRepository;
        this.tradeChatReadRepository = tradeChatReadRepository;
    }

    public DashboardCounts getCountsForCurrentUser() {
        UUID userId = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND))
                .getId();

        long activeTrades = countActiveTrades(userId);
        long incomingChats = countUnreadChats(userId);
        return new DashboardCounts(activeTrades, incomingChats);
    }

    private long countActiveTrades(UUID userId) {
        // Consider trades not finalized yet as "đang xử lý"
        long pending = tradeRepository.countByUserIdAndStatus(userId, TradeStatus.PENDING);
        long paid = tradeRepository.countByUserIdAndStatus(userId, TradeStatus.PAID);
        long confirmed = tradeRepository.countByUserIdAndStatus(userId, TradeStatus.CONFIRMED);
        long disputed = tradeRepository.countByUserIdAndStatus(userId, TradeStatus.DISPUTED);
        return pending + paid + confirmed + disputed;
    }

    private long countUnreadChats(UUID userId) {
        List<Trade> trades = tradeRepository.findTradesWithChatsByUser(userId);
        if (trades.isEmpty()) {
            return 0;
        }
        List<UUID> tradeIds = trades.stream().map(Trade::getId).toList();

        Map<UUID, TradeChatRead> readMap = tradeChatReadRepository
                .findByUserIdAndTradeIdIn(userId, tradeIds)
                .stream()
                .collect(Collectors.toMap(r -> r.getTrade().getId(), r -> r));

        List<TradeChat> latestMessages = tradeChatRepository.findLatestMessagesByTradeIds(tradeIds);

        return latestMessages.stream()
                .filter(msg -> {
                    LocalDateTime lastRead = Optional.ofNullable(readMap.get(msg.getTrade().getId()))
                            .map(TradeChatRead::getLastReadAt)
                            .orElse(LocalDateTime.MIN);
                    return (msg.getSenderId() == null || !userId.equals(msg.getSenderId()))
                            && msg.getTimestamp().isAfter(lastRead);
                })
                .count();
    }

    @Transactional
    public void markTradeChatRead(UUID tradeId) {
        UUID userId = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND))
                .getId();

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));
        if (!trade.getBuyer().getId().equals(userId) && !trade.getSeller().getId().equals(userId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        TradeChatRead record = tradeChatReadRepository.findExisting(tradeId, userId)
                .orElseGet(() -> {
                    TradeChatRead r = new TradeChatRead();
                    r.setTrade(trade);
                    r.setUserId(userId);
                    return r;
                });
        record.setLastReadAt(LocalDateTime.now());
        tradeChatReadRepository.save(record);
    }
}
