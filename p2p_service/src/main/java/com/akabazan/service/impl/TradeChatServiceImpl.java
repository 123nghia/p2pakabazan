package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.TradeChatRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.TradeChat;
import com.akabazan.repository.entity.User;
import com.akabazan.service.TradeChatService;
import com.akabazan.service.dto.TradeChatResult;
import com.akabazan.service.dto.TradeChatThreadResult;
import com.akabazan.service.dto.TradeMapper;
import com.akabazan.service.dto.TradeResult;
import com.akabazan.service.dto.TradeChatMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TradeChatServiceImpl implements TradeChatService {

    private final TradeRepository tradeRepository;
    private final TradeChatRepository tradeChatRepository;

    private enum RecipientRole {
        BUYER, SELLER, ALL
    }

    public TradeChatServiceImpl(TradeRepository tradeRepository,
                                TradeChatRepository tradeChatRepository) {
        this.tradeRepository = tradeRepository;
        this.tradeChatRepository = tradeChatRepository;
    }

    @Override
    @Transactional
    public TradeChatResult sendMessage(Long tradeId, String message) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        TradeChat chat = new TradeChat();
        chat.setTrade(trade);
        chat.setSenderId(getCurrentUserId());
        chat.setMessage(message);
        chat.setTimestamp(LocalDateTime.now());
        chat.setRecipientRole(RecipientRole.ALL.name());

        return TradeChatMapper.toResult(tradeChatRepository.save(chat));
    }

    @Override
    public List<TradeChatResult> getMessages(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));
        RecipientRole viewerRole = determineUserRoleForTrade(trade, getCurrentUserId());
        return tradeChatRepository.findByTradeIdOrderByTimestampAsc(tradeId).stream()
                .filter(chat -> isVisibleForRecipient(chat.getRecipientRole(), viewerRole))
                .map(TradeChatMapper::toResult)
                .collect(Collectors.toList());
    }

    @Override
    public List<TradeChatThreadResult> getChatThreadsForCurrentUser() {
        Long userId = getCurrentUserId();
        List<Trade> trades = tradeRepository.findTradesWithChatsByUser(userId);
        if (trades.isEmpty()) {
            return List.of();
        }

        List<Long> tradeIds = trades.stream()
                .map(Trade::getId)
                .toList();

        Map<Long, TradeChatResult> lastMessages = tradeChatRepository.findLatestMessagesByTradeIds(tradeIds)
                .stream()
                .collect(Collectors.toMap(
                        chat -> chat.getTrade().getId(),
                        chat -> TradeChatMapper.toResult(chat),
                        (existing, ignored) -> existing
                ));

        List<TradeChatThreadResult> threads = trades.stream()
                .map(trade -> {
                    TradeChatThreadResult thread = new TradeChatThreadResult();
                    TradeResult tradeResult = TradeMapper.toResult(trade);
                    thread.setTrade(tradeResult);
                    String counterpartyName = resolveCounterpartyName(trade, userId);
                    tradeResult.setCounterparty(counterpartyName);
                    thread.setCounterpartyName(counterpartyName);
                    RecipientRole viewerRole = determineUserRoleForTrade(trade, userId);
                    TradeChatResult lastVisibleMessage = resolveLastVisibleMessage(
                            trade.getId(),
                            viewerRole,
                            lastMessages.get(trade.getId()));
                    thread.setLastMessage(lastVisibleMessage);
                    return thread;
                })
                .collect(Collectors.toList());

        threads.sort(Comparator
                .comparing((TradeChatThreadResult thread) -> thread.getLastMessage() != null
                        ? thread.getLastMessage().getTimestamp()
                        : LocalDateTime.MIN)
                .reversed());

        return threads;
    }

    private String resolveCounterpartyName(Trade trade, Long currentUserId) {
        if (trade == null) {
            return null;
        }
        if (trade.getBuyer() != null && trade.getBuyer().getId().equals(currentUserId)) {
            return extractDisplayName(trade.getSeller());
        }
        if (trade.getSeller() != null && trade.getSeller().getId().equals(currentUserId)) {
            return extractDisplayName(trade.getBuyer());
        }
        return extractDisplayName(trade.getSeller() != null ? trade.getSeller() : trade.getBuyer());
    }

    private RecipientRole determineUserRoleForTrade(Trade trade, Long userId) {
        if (trade == null || userId == null) {
            return RecipientRole.ALL;
        }
        if (trade.getBuyer() != null && userId.equals(trade.getBuyer().getId())) {
            return RecipientRole.BUYER;
        }
        if (trade.getSeller() != null && userId.equals(trade.getSeller().getId())) {
            return RecipientRole.SELLER;
        }
        return RecipientRole.ALL;
    }

    private boolean isVisibleForRecipient(String recipientRole, RecipientRole viewerRole) {
        if (recipientRole == null || recipientRole.isBlank()) {
            return true;
        }
        if (RecipientRole.ALL.name().equalsIgnoreCase(recipientRole)) {
            return true;
        }
        if (viewerRole == null) {
            return true;
        }
        return recipientRole.equalsIgnoreCase(viewerRole.name());
    }

    private TradeChatResult resolveLastVisibleMessage(Long tradeId,
                                                      RecipientRole viewerRole,
                                                      TradeChatResult candidate) {
        if (candidate != null && isVisibleForRecipient(candidate.getRecipientRole(), viewerRole)) {
            return candidate;
        }

        TradeChat latestVisible = null;
        List<TradeChat> chats = tradeChatRepository.findByTradeIdOrderByTimestampAsc(tradeId);
        for (TradeChat chat : chats) {
            if (isVisibleForRecipient(chat.getRecipientRole(), viewerRole)) {
                latestVisible = chat;
            }
        }
        return latestVisible != null ? TradeChatMapper.toResult(latestVisible) : null;
    }

    private String extractDisplayName(User user) {
        if (user == null) {
            return null;
        }
        String email = user.getEmail();
        if (email == null) {
            return null;
        }
        int atIndex = email.indexOf('@');
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }

    private Long getCurrentUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
